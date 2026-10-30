# Repository Guidelines

## Project Structure & Module Organization

Java 21 / Spring Boot 3.3 backend. Sources are under `src/main/java/com/plzgraduate/myongjigraduatebe`. Feature packages (`graduation`, `lecture`, `user`, `timetable`, etc.) use hexagonal layers: `api`, `application/usecase`, `application/service`, `application/port`, `domain`, and `infrastructure/adapter/persistence`. Shared configuration lives in `core`. Tests mirror sources under `src/test/java`, including `fixture`. Configuration and Flyway migrations are under `src/main/resources`; operational documentation and local helpers are in `docs/` and `infra/`.

## Build, Test, and Development Commands

- `./gradlew build`: compile, test, assemble.
- `./gradlew test --tests '*ClassNameTest'`: focused JUnit tests.
- `./gradlew clean test jacocoTestReport --no-daemon`: CI tests and coverage.
- `docker compose -f infra/localcontainer/docker-compose.yml up -d`: local MySQL/Redis.
- `./gradlew bootRun`: API startup; first verify the selected profile and database.
- `python3 -m unittest discover -s infra/localbackup -p 'test_*.py'`: offline snapshot-helper tests.

## Coding Style & Naming Conventions

Use 4-space indentation for new Java code; preserve surrounding formatting. Packages are lowercase; classes describe roles, e.g. `FindLectureInfoService`. Prefer constructor injection, Lombok, and existing `@WebAdapter`, `@UseCase`, `@PersistenceAdapter`, and `@LoginUser` conventions.

## Testing Guidelines

Use JUnit 5, Mockito, Spring Boot Test, and Testcontainers. Name classes `*Test` or `*IntegrationTest`. Keep domain tests isolated; test persistence, security, and HTTP contracts at their boundaries. Settings: `src/test/resources/application-test.yml`. Report actual test results, not assumed coverage.

## Branch Naming

Before creating or renaming a branch, check this guidance and established local and remote branch names. Use `<type>/DEV-<number>`: `feature` for new functionality, `fix` for bug fixes, `refactor` for structural changes, and `chore` for maintenance. Examples: `feature/DEV-122`, `fix/DEV-121`, `refactor/DEV-123`, and `chore/DEV-124`.

Follow this repository convention instead of the default `codex/` prefix; do not use a bare issue key such as `DEV-121` by default. Use an explicitly requested branch name when provided. Do not rename unrelated existing branches to normalize historical names.

## Commit & Pull Request Guidelines

For individual commits, use `type: 한국어 설명`, for example `feat: 정책 근거 조회 기능 추가`, without an issue-key prefix. Allowed types are `feat`, `fix`, `refactor`, `style`, `chore`, `test`, `build`, and `docs`. Use one space after the colon.

For PR titles and squash-merge commit titles, use `[DEV-번호] 한국어 설명`, for example `[DEV-116] 정책 근거 조회 기능 추가`, without a type prefix. Use the actual issue number and one space after the closing bracket.

Do not include literal backticks in commit messages or PR titles. Apply these conventions to new commits and PRs without rewriting historical messages.

Follow `.github/PULL_REQUEST_TEMPLATE.md`: linked issue, behavior changes, reviewer focus, unresolved decisions. Preserve unrelated changes; never stage everything indiscriminately. Commit, push, and deploy only when requested.

## Security & Configuration Tips

Never commit dumps, credentials, or snapshot `state/`. The restored snapshot contains personal data: do not start the app against it or send its records to models, embeddings, tracing, or code indexes. Use synthetic fixtures externally. Review profile DDL/Flyway behavior before any database access.

## DEV-116 Workflow

Read [the implementation plan](docs/DEV-116_AI_IMPLEMENTATION_PLAN.md) before DEV-116 work. Update the relevant task, evidence, decisions, and next step after each work unit; check off only verified results. Do not expand the authorized phase. Use `mju-policy-change` for graduation-policy changes and `mju-local-data` for snapshot/development-data work. For OpenAI/Codex questions, use OpenAI Docs MCP with `openai-docs` when available; otherwise use official documentation. Repository guidance never grants extra permissions.
