package com.plzgraduate.myongjigraduatebe.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.yaml.snakeyaml.Yaml;

class ProductionDeploymentWorkflowTest {
    @TempDir
    Path temp;

    @Test
    void preservesRunningRedisAndUsesReleaseImageAndNamespace() throws Exception {
        var result = run(Map.of());
        assertThat(result.exitCode()).as(result.output()).isZero();
        assertThat(result.trace()).doesNotContain("rm -f mg-redis", "start mg-redis", "--name mg-redis",
            "volume rm", "--volumes", "FLUSHDB", "FLUSHALL");
        assertThat(result.trace()).contains("pull test/image:gha-123-2",
            "--memory=640m --memory-reservation=512m --memory-swap=1g",
            "--label autoheal=true", "--health-start-period=300s",
            "-e JAVA_OPTS=-Xms64m -Xmx256m -XX:ActiveProcessorCount=2",
            "-e SHARED_MASTER_CACHE_ENABLED=true", "-e SHARED_MASTER_CACHE_NAMESPACE=prod",
            "-e SHARED_MASTER_CACHE_VERSION=gha-123-2",
            "-e TAKEN_LECTURE_CACHE_MODE=off", "-e TAKEN_LECTURE_CACHE_NAMESPACE=prod-dev121-v3",
            "test/image:gha-123-2");
        assertThat(result.trace().indexOf("pull test/image:gha-123-2"))
            .isLessThan(result.trace().indexOf("rm -f mju-graduate-server"));
    }

    @Test
    void startsExistingStoppedRedisWithoutReplacingIt() throws Exception {
        var result = run(Map.of("MOCK_REDIS_STATE", "stopped"));
        assertThat(result.exitCode()).as(result.output()).isZero();
        assertThat(result.trace()).contains("start mg-redis").doesNotContain("rm -f mg-redis", "--name mg-redis");
    }

    @Test
    void bootstrapsRedisOnlyWhenMissing() throws Exception {
        var result = run(Map.of("MOCK_REDIS_STATE", "absent"));
        assertThat(result.exitCode()).as(result.output()).isZero();
        assertThat(result.trace()).contains("volume create mg-redis-data", "run -d --name mg-redis")
            .doesNotContain("rm -f mg-redis");
    }

    @Test
    void redisReadinessFailureKeepsExistingApp() throws Exception {
        var result = run(Map.of("MOCK_REDIS_READY", "false"));
        assertThat(result.exitCode()).as(result.output()).isNotZero();
        assertThat(result.trace()).doesNotContain("rm -f mju-graduate-server", "run -d --name mju-graduate-server");
    }

    @Test
    void imagePullFailureKeepsExistingApp() throws Exception {
        var result = run(Map.of("MOCK_PULL_FAIL", "true"));
        assertThat(result.exitCode()).as(result.output()).isNotZero();
        assertThat(result.trace()).doesNotContain("rm -f mju-graduate-server", "run -d --name mju-graduate-server");
    }

    @Test
    void unhealthyAppDoesNotReportSuccessfulDeployment() throws Exception {
        var result = run(Map.of("MOCK_APP_READY", "false"));
        assertThat(result.exitCode()).as(result.output()).isNotZero();
        assertThat(result.output()).contains("Application did not become healthy").doesNotContain("Deployment ready:");
    }

    @Test
    void invalidFlagDoesNotTouchDocker() throws Exception {
        var result = run(Map.of("SHARED_MASTER_CACHE_ENABLED", "invalid"));
        assertThat(result.exitCode()).as(result.output()).isNotZero();
        assertThat(result.trace()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"off", "shadow", "on"})
    void forwardsTakenLectureCacheSettingsToApp(String mode) throws Exception {
        var result = run(Map.of("TAKEN_LECTURE_CACHE_MODE", mode,
            "TAKEN_LECTURE_CACHE_NAMESPACE", "prod-dev121-restore-2"));
        assertThat(result.exitCode()).as(result.output()).isZero();
        assertThat(result.trace()).contains("-e TAKEN_LECTURE_CACHE_MODE=" + mode,
            "-e TAKEN_LECTURE_CACHE_NAMESPACE=prod-dev121-restore-2");
    }

    @Test
    void invalidTakenLectureCacheModeDoesNotTouchDocker() throws Exception {
        var result = run(Map.of("TAKEN_LECTURE_CACHE_MODE", "enabled"));
        assertThat(result.exitCode()).as(result.output()).isNotZero();
        assertThat(result.output()).contains("TAKEN_LECTURE_CACHE_MODE must be off, shadow, or on");
        assertThat(result.trace()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "prod:shared", "prod cache", "prod\ncache"})
    void invalidTakenLectureCacheNamespaceDoesNotTouchDocker(String namespace) throws Exception {
        var result = run(Map.of("TAKEN_LECTURE_CACHE_NAMESPACE", namespace));
        assertThat(result.exitCode()).as(result.output()).isNotZero();
        assertThat(result.output()).contains("TAKEN_LECTURE_CACHE_NAMESPACE must contain only");
        assertThat(result.trace()).isEmpty();
    }

    @Test
    void validatesReleaseAndRunsTestsBeforePublishingOrTouchingProduction() throws Exception {
        Map<String, Object> job = map(map(workflow().get("jobs")).get("build"));
        var steps = ((List<?>) job.get("steps")).stream().map(ProductionDeploymentWorkflowTest::map).toList();
        var names = steps.stream().map(s -> s.get("name").toString()).toList();
        assertThat(step("Check DEV-121 release inputs").get("run").toString())
            .contains("python3 infra/cache/check_release.py");
        assertThat(step("Test and build").get("run"))
            .hasToString("./gradlew clean build --no-daemon");
        assertThat(names.indexOf("Check DEV-121 release inputs")).isLessThan(names.indexOf("Test and build"));
        assertThat(names.indexOf("Test and build")).isLessThan(names.indexOf("Build and Push Docker image"));
        assertThat(names.indexOf("Build and Push Docker image")).isLessThan(names.indexOf("Prepare production host memory"));
    }

    @Test
    void workflowSerializesDeploymentsAndVersionsEveryAttempt() throws Exception {
        Map<String, Object> workflow = workflow();
        assertThat(map(workflow.get("concurrency"))).containsEntry("group", "production-deploy")
            .containsEntry("cancel-in-progress", false);
        Map<String, Object> job = map(map(workflow.get("jobs")).get("build"));
        assertThat(map(job.get("env"))).containsEntry("DEPLOYMENT_ID", "gha-${{ github.run_id }}-${{ github.run_attempt }}")
            .containsEntry("SHARED_MASTER_CACHE_ENABLED", "${{ vars.SHARED_MASTER_CACHE_ENABLED || 'false' }}")
            .containsEntry("TAKEN_LECTURE_CACHE_MODE", "${{ vars.TAKEN_LECTURE_CACHE_MODE || 'off' }}")
            .containsEntry("TAKEN_LECTURE_CACHE_NAMESPACE", "${{ vars.TAKEN_LECTURE_CACHE_NAMESPACE || 'prod-dev121-v3' }}");
        assertThat(map(step("Deploy via SSH").get("with")))
            .containsEntry("envs", "DEPLOYMENT_ID,DOCKER_IMAGE,SHARED_MASTER_CACHE_ENABLED,TAKEN_LECTURE_CACHE_MODE,TAKEN_LECTURE_CACHE_NAMESPACE");
        assertThat(map(step("Build and Push Docker image").get("with")).get("tags").toString())
            .contains("${{ env.DOCKER_IMAGE }}:${{ env.DEPLOYMENT_ID }}");
    }

    private Result run(Map<String, String> overrides) throws Exception {
        String script = map(step("Deploy via SSH").get("with")).get("script").toString()
            .replaceAll("\\$\\{\\{ secrets\\.[A-Z_]+ }}", "test-secret");
        Path scriptFile = temp.resolve("deploy.sh");
        Files.writeString(scriptFile, script);
        Path mockDocker = temp.resolve("docker");
        try (var source = getClass().getResourceAsStream("/deployment/docker")) {
            Files.copy(source, mockDocker);
        }
        Path mockSleep = temp.resolve("sleep");
        Files.writeString(mockSleep, "#!/bin/sh\nexit 0\n");
        for (var executable : List.of(mockDocker, mockSleep)) {
            Files.setPosixFilePermissions(executable, PosixFilePermissions.fromString("rwx------"));
        }
        Path trace = temp.resolve("docker.log");
        Path output = temp.resolve("output.log");
        ProcessBuilder builder = new ProcessBuilder("bash", scriptFile.toString());
        builder.redirectErrorStream(true).redirectOutput(output.toFile());
        Map<String, String> env = builder.environment();
        env.put("PATH", temp + ":" + env.get("PATH"));
        env.putAll(Map.of("MOCK_TRACE", trace.toString(), "MOCK_REDIS_STATE", "running",
            "MOCK_REDIS_READY", "true", "MOCK_APP_READY", "true", "MOCK_PULL_FAIL", "false",
            "DEPLOYMENT_ID", "gha-123-2", "DOCKER_IMAGE", "test/image", "SHARED_MASTER_CACHE_ENABLED", "true"));
        env.put("TAKEN_LECTURE_CACHE_MODE", "off");
        env.put("TAKEN_LECTURE_CACHE_NAMESPACE", "prod-dev121-v3");
        env.putAll(overrides);
        Process process = builder.start();
        try {
            assertThat(process.waitFor(15, TimeUnit.SECONDS)).isTrue();
            return new Result(process.exitValue(), Files.exists(trace) ? Files.readString(trace) : "",
                Files.readString(output));
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    private Map<String, Object> workflow() throws Exception {
        try (var source = Files.newInputStream(Path.of(".github/workflows/deploy.yml"))) {
            return new Yaml().load(source);
        }
    }

    private Map<String, Object> step(String name) throws Exception {
        Map<String, Object> job = map(map(workflow().get("jobs")).get("build"));
        return ((List<?>) job.get("steps")).stream().map(ProductionDeploymentWorkflowTest::map)
            .filter(step -> name.equals(step.get("name"))).findFirst().orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object value) {
        return (Map<String, Object>) value;
    }

    private record Result(int exitCode, String trace, String output) {}
}
