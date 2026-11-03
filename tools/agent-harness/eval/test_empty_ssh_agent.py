import os
import socket
import struct
import tempfile
import threading
import time
import unittest
from unittest import mock

from empty_ssh_agent import EmptySshAgent


class EmptySshAgentTest(unittest.TestCase):
    def exchange(self, agent, connection, body):
        connection.sendall(struct.pack('>I', len(body)) + body)
        size = struct.unpack('>I', agent.read_exact(connection, 4))[0]
        return agent.read_exact(connection, size)

    def request(self, agent, body):
        with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as connection:
            connection.settimeout(2)
            connection.connect(str(agent.path))
            connection.sendall(struct.pack('>I', len(body)) + body)
            size = struct.unpack('>I', agent.read_exact(connection, 4))[0]
            return agent.read_exact(connection, size)

    def test_no_keys_no_signatures_and_no_host_agent(self):
        original = os.environ.get('SSH_AUTH_SOCK')
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            with EmptySshAgent(directory) as agent:
                self.assertEqual(self.request(agent, b'\x0b'), b'\x0c\x00\x00\x00\x00')
                for message in [b'\x0dsynthetic-sign-request', b'\x11synthetic-add-key', b'\x0bextra']:
                    self.assertEqual(self.request(agent, message), b'\x05')
                self.assertEqual(agent.path.stat().st_mode & 0o777, 0o600)
                self.assertEqual(agent.environment()['SSH_AUTH_SOCK'], str(agent.path))
                self.assertEqual(os.environ.get('SSH_AUTH_SOCK'), original)
            self.assertFalse(agent.path.exists())
            self.assertFalse(agent.thread.is_alive())

    def test_oversized_and_truncated_requests_do_not_stop_agent(self):
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            with EmptySshAgent(directory) as agent:
                for payload in [struct.pack('>I', 4097), struct.pack('>I', 2) + b'\x0b']:
                    with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as connection:
                        connection.connect(str(agent.path))
                        connection.sendall(payload)
                self.assertEqual(self.request(agent, b'\x0b'), b'\x0c\x00\x00\x00\x00')

    def test_exception_still_closes_socket_and_thread(self):
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            with self.assertRaisesRegex(RuntimeError, 'synthetic failure'):
                with EmptySshAgent(directory) as agent:
                    raise RuntimeError('synthetic failure')
            self.assertFalse(agent.path.exists())
            self.assertFalse(agent.thread.is_alive())

    def test_failed_bind_preserves_an_existing_path(self):
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            agent = EmptySshAgent(directory)
            agent.path.write_text('unrelated synthetic file')
            with self.assertRaises(OSError):
                agent.__enter__()
            self.assertEqual(agent.path.read_text(), 'unrelated synthetic file')

    def test_startup_failure_removes_its_own_socket(self):
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            agent = EmptySshAgent(directory)
            with mock.patch.object(threading.Thread, 'start', side_effect=RuntimeError('synthetic startup failure')):
                with self.assertRaisesRegex(RuntimeError, 'synthetic startup failure'):
                    agent.__enter__()
            self.assertFalse(agent.path.exists())

    def test_slow_sender_cannot_delay_context_cleanup(self):
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            sender_stop = threading.Event()
            connection = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
            sender = None
            def send_slowly():
                while not sender_stop.wait(0.02):
                    try:
                        connection.sendall(b'x')
                    except OSError:
                        break
            try:
                with EmptySshAgent(directory) as agent:
                    connection.connect(str(agent.path))
                    connection.sendall(struct.pack('>I', 4096) + b'\x0d')
                    sender = threading.Thread(target=send_slowly)
                    sender.start()
                    time.sleep(0.1)
                    started = time.monotonic()
                self.assertLess(time.monotonic() - started, 2)
                self.assertFalse(agent.path.exists())
                self.assertFalse(agent.thread.is_alive())
            finally:
                sender_stop.set()
                connection.close()
                if sender is not None:
                    sender.join(timeout=2)

    def test_cached_connection_survives_idle_and_other_clients(self):
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            with EmptySshAgent(directory) as agent:
                with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as connection:
                    connection.connect(str(agent.path))
                    self.assertEqual(self.exchange(agent, connection, b'\x0b'), b'\x0c\x00\x00\x00\x00')
                    time.sleep(0.65)
                    self.assertEqual(self.request(agent, b'\x0b'), b'\x0c\x00\x00\x00\x00')
                    self.assertEqual(self.exchange(agent, connection, b'\x0b'), b'\x0c\x00\x00\x00\x00')
                    self.assertEqual(self.exchange(agent, connection, b'\x0dsynthetic'), b'\x05')

    def test_request_log_has_a_fixed_bound(self):
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            with EmptySshAgent(directory) as agent:
                with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as connection:
                    connection.connect(str(agent.path))
                    for _ in range(260):
                        self.assertEqual(self.exchange(agent, connection, b'\x0b'), b'\x0c\x00\x00\x00\x00')
                self.assertEqual(agent.requests, [11] * 256)

    def test_worker_start_failure_closes_connection_and_fails_cleanup(self):
        with tempfile.TemporaryDirectory(prefix='dev122-empty-agent-') as directory:
            agent = EmptySshAgent(directory)
            with self.assertRaisesRegex(RuntimeError, 'worker failed to start'):
                with agent:
                    with mock.patch.object(threading.Thread, 'start', side_effect=RuntimeError('synthetic worker failure')):
                        with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as connection:
                            connection.connect(str(agent.path))
                            self.assertTrue(agent.stop.wait(2))
            self.assertFalse(agent.path.exists())
            self.assertFalse(agent.connections)


if __name__ == '__main__':
    unittest.main()
