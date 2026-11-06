"""Supply a task-local SSH agent with no keys to host-side sbx subprocesses."""
import os
from pathlib import Path
import socket
import struct
import threading
import time


class EmptySshAgent:
    """No host agent access, key storage, signing, or network sockets."""

    def __init__(self, directory):
        self.path = Path(directory) / 'empty-agent.sock'
        self.requests = []
        self.stop = threading.Event()
        self.lock = threading.Lock()
        self.connections = set()
        self.workers = set()
        self.worker_start_failed = False

    def __enter__(self):
        self.server = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
        bound = False
        try:
            self.server.bind(str(self.path))
            bound = True
            self.path.chmod(0o600)
            self.server.listen(4)
            self.server.settimeout(0.2)
            self.thread = threading.Thread(target=self.serve, daemon=True)
            self.thread.start()
            return self
        except BaseException:
            self.server.close()
            if bound:
                self.path.unlink()
            raise

    def environment(self):
        return {**os.environ, 'SSH_AUTH_SOCK': str(self.path)}

    @staticmethod
    def read_exact(connection, length, stop=None, deadline=None):
        data = b''
        if deadline is None:
            deadline = time.monotonic() + 0.5
        while len(data) < length:
            if stop is not None and stop.is_set():
                raise EOFError('Synthetic agent is stopping')
            remaining = deadline - time.monotonic()
            if remaining <= 0:
                raise TimeoutError('Synthetic agent read deadline exceeded')
            connection.settimeout(remaining)
            chunk = connection.recv(length - len(data))
            if not chunk:
                raise EOFError('Truncated synthetic agent request')
            data += chunk
        return data

    def serve(self):
        while not self.stop.is_set():
            try:
                connection, _ = self.server.accept()
            except socket.timeout:
                continue
            except OSError:
                return
            with self.lock:
                if self.stop.is_set() or len(self.connections) >= 8:
                    connection.close()
                    continue
                self.connections.add(connection)
                self.workers = {worker for worker in self.workers if worker.is_alive()}
                worker = threading.Thread(target=self.handle, args=(connection,), daemon=True)
                self.workers.add(worker)
                try:
                    worker.start()
                except RuntimeError:
                    self.worker_start_failed = True
                    self.connections.remove(connection)
                    self.workers.remove(worker)
                    connection.close()
                    self.stop.set()

    def handle(self, connection):
        try:
            while not self.stop.is_set():
                # A cached SSH connection can be idle indefinitely. The frame
                # deadline begins at its first byte, not during idle waiting.
                connection.settimeout(0.2)
                try:
                    first = connection.recv(1)
                except socket.timeout:
                    continue
                if not first:
                    return
                deadline = time.monotonic() + 0.5
                header = first + self.read_exact(connection, 3, self.stop, deadline)
                length = struct.unpack('>I', header)[0]
                if not 1 <= length <= 4096:
                    return
                request = self.read_exact(connection, length, self.stop, deadline)
                with self.lock:
                    if len(self.requests) < 256:
                        self.requests.append(request[0])
                response = b'\x0c\x00\x00\x00\x00' if request == b'\x0b' else b'\x05'
                connection.sendall(struct.pack('>I', len(response)) + response)
        except (OSError, EOFError):
            pass
        finally:
            connection.close()
            with self.lock:
                self.connections.discard(connection)

    def __exit__(self, *exception):
        with self.lock:
            self.stop.set()
            connections = list(self.connections)
            workers = list(self.workers)
        try:
            self.server.close()
            for connection in connections:
                try:
                    connection.shutdown(socket.SHUT_RDWR)
                except OSError:
                    pass
                connection.close()
            self.thread.join(timeout=2)
            for worker in workers:
                worker.join(timeout=2)
            if self.thread.is_alive() or any(worker.is_alive() for worker in workers):
                raise RuntimeError('Synthetic SSH agent did not terminate')
            if self.worker_start_failed:
                raise RuntimeError('Synthetic SSH agent worker failed to start')
        finally:
            self.path.unlink(missing_ok=True)
