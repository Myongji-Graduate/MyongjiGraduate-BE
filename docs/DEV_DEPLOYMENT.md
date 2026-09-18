# 개발 서버 배포 (Render)

개발 서버: https://myongjigraduate-be.onrender.com

Swagger: https://myongjigraduate-be.onrender.com/swagger-ui/index.html

## 배포 흐름

1. 작업 브랜치에서 `develop` 대상으로 PR을 만든다.
2. GitHub Actions에서 `Dev Build`, 테스트·커버리지, SonarQube 검사를 확인한다.
3. PR을 `develop`에 머지한다.
4. Render가 연결된 `develop` 커밋을 저장소의 Dockerfile로 빌드하고 배포한다.
5. Render의 배포 기록에서 배포 성공 여부와 실제 배포된 커밋을 확인한다.

`Dev Build`는 빌드 검사다. 이 검사의 성공만으로 Render 배포 완료를 의미하지 않는다.
Swagger 접속 성공도 최신 커밋이 배포되었다는 증거는 아니므로 배포 기록의 커밋을 확인한다.

## Render에서 확인할 설정

Render 대시보드 → `MyongjiGraduate-BE` 서비스 → Settings에서 다음을 확인한다.
이 문서는 필요한 구성을 설명하며, GitHub 워크플로우 변경만으로 Render 설정이 바뀌지는 않는다.

| 항목 | 설정 |
| --- | --- |
| Source | 연결된 GitHub 저장소 `Myongji-Graduate/MyongjiGraduate-BE` |
| Branch | `develop` |
| Runtime | Docker (저장소 Dockerfile로 빌드) |
| Auto-Deploy | `After CI Checks Pass` 권장 |

`After CI Checks Pass`는 해당 커밋의 모든 CI 검사가 통과한 뒤 배포한다.
`On Commit`은 연결된 브랜치에 푸시되는 즉시 배포하며, CI 실패가 배포를 막지 않는다.
`Off`는 자동 배포를 하지 않으므로 수동 배포 또는 별도의 Deploy Hook 연결이 필요하다.

환경 변수 `SPRING_PROFILES_ACTIVE=dev`도 확인한다. 공용 Dockerfile의 기본 프로필은 `prod`다.

이 구성은 GitHub 연동으로 저장소를 직접 빌드하는 서비스를 전제로 한다.
Docker Hub 이미지를 가져오는 서비스라면 해당 이미지의 빌드·업로드와 배포 트리거가 별도로 필요하다.

## GitHub Actions 역할

- `.github/workflows/dev-deploy.yml`: `develop` 대상 PR과 `develop` 푸시에서 Java 빌드 검사. 수동 실행도 지원한다.
- `.github/workflows/pr-test.yml`: 기존 테스트·커버리지 검사.
- `.github/workflows/sonarqube.yml`: 기존 코드 품질 검사.

개발용 Docker Hub 업로드와 `STAGING_HOST`에 대한 SSH 배포는 사용하지 않는다.
Render에서 Dockerfile을 직접 빌드하므로 GitHub Actions에 Render Deploy Hook이나 SSH 키를 추가할 필요가 없다.
기존 시크릿은 다른 사용처를 확인하기 전까지 삭제하지 않는다.

운영은 `.github/workflows/deploy.yml`이 `main` 푸시에 실행하는 기존 SSH 배포를 유지한다.
공용 Dockerfile과 운영 배포 설정은 이 변경에 포함하지 않는다.

## 이전 SSH 실패 표시

설정 변경은 머지 이후 새로 실행되는 작업에 적용된다. 예전 실행의 실패 기록은 남으며,
예전 실행을 재실행하면 그 커밋의 SSH 배포 설정이 다시 사용될 수 있다.

참고: [Render 배포 및 자동 배포 설정](https://render.com/docs/deploys)
