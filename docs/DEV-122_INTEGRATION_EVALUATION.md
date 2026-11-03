# DEV-122 — DB/Redis 평가 환경 준비

상태: **CLI 인증·VM 기본 점검·Java 21/이미지/의존성 준비와 T04/T05 모델 없는 사전 검증 완료**.
정상·회귀·복구 8행과 정리 증거는 [사전 검증 보고서](DEV-122_INTEGRATION_PREFLIGHT_REPORT.md)에 있다.
후속 Codex 명령·MCP·합성 인증/VM 간 경계와 timeout 점검도 완료했다.
[경계 검증 보고서](DEV-122_CODEX_VM_BOUNDARY_REPORT.md)에 실제 증거를 기록했다.
사용자 승인으로 Docker OAuth 등록과 소스 없는 VM의 초기 대체 값 확인도 완료했다.
후속 [provider·네트워크 점검](DEV-122_PROVIDER_NETWORK_REPORT.md)에서 전용 kit/프로필·로컬 HTTP 정책과
실제 사용량 GET 1회 수용을 확인했다. SSH 중계·HTTPS 차단 주체·갱신과 전체 평가 준비는 미완료이며 기존 P2 결과와 별도 증거다.
실제 관찰값은 [준비 기록](../tools/agent-harness/eval/integration-runtime-preparation.json)에 있다.
사용자가 로그인 진행을 승인했다. 첫 인증과 간격을 둔 한 차례 재시도 모두 Docker 인증 서버의
`Global rate limit exceeded`로 종료됐다. 이후 사용자의 재시도 요청으로 세 번째 로그인을 실행했지만
같은 오류로 종료됐다. 이후 사용자가 브라우저 로그인을 완료했다고 알려준 뒤 네 번째 CLI 인증이
종료 코드 0으로 성공했다. 앞선 오류만으로 브라우저 로그인 후에도 진행 불가라고 판단하지 않는다.
인증 토큰/코드는 평가 자료에 저장하지 않는다.

후속 조사에서 같은 오류의 [Docker 이슈 #716](https://github.com/docker/sbx-releases/issues/716)을
확인했다. 담당자는 10월 31일 device flow에서 대기/재시도를 유도할 응답이 종료 오류로 돌아오는
문제를 설명했고, 11월 1일 SBX 수정 PR과 릴리스 대기를 안내했다. 현재 증상은 이 알려진 문제와
일치하지만 우리 요청의 서버 내부 로그로 원인을 확정한 것은 아니다. 10월 28일 배포된 0.50.1로
반복 로그인한다고 해결된다고 보장할 수는 없었으나, 이번에는 같은 버전에서 인증이 성공했다.

## 현재 확인한 VM 증거

- 새 도구의 초기 네트워크 정책이 없어 `deny-all`로 초기화한 뒤 source path 없는
  `dev122-isolation-probe` shell VM을 생성했다. 공유 스킬 off, VM별 domain/IPv4/IPv6 deny가 적용됐다.
- VM root에서 합성 호스트 canary 파일과 개인 Docker socket 경로가 보이지 않았다.
  호스트/VM Docker engine ID가 다르고 사용자 홈 mount·공유 스킬·주입 secret 파일은 관찰되지 않았다.
- 호스트 canary 서버의 자체 연결은 성공했고 VM 요청 수는 0이다. host alias/VM loopback/
  공개 domain/IPv4/IPv6 연결 5건은 모두 실패했다. 연결 실패만으로 모든 우회 경로의 차단을 입증하지 않는다.
- 기본 점검 9건과 원시 결과는 `build/agent-harness/dev122-vm-basic-probe/`에 있다.
  기본 VM은 Java 25.0.4.1/Node 22.22.1/Git 2.53.0이며 후속 workload에 Java 21.0.13+9를 별도 설치했다.
- 고정 기준선의 허용 파일과 이미지·의존성을 파일로 복사하고 source/CLI/cache/image hash를 보존했다.
  정상과 복구는 unit 21/integration 31통과, T04/T05는 unit 21통과와 지정 integration oracle 1실패다.
- 원본 테스트 198파일·제품 2파일 복구 지문과 전체 container/volume/custom network 0을 확인했고 VM은 정지했다.
  이 VM에는 정상 이력과 결과가 남아 있어 실제 비교 에이전트에는 재사용하지 않는다. 모델 실행은 0회다.
- 2026-11-03 별도 빈 VM에서 저장된 OAuth 사용과 auth/provider의 공개 대체 값 일치를 확인했다.
  격리한 Codex의 MCP0/스킬0, account=null/requiresOpenaiAuth=false를 보존했다. 임시 VM은 삭제했고
  승인된 전역 OAuth 등록 1개는 유지한다. [OAuth 결과](../tools/agent-harness/eval/codex-vm-oauth-result.json)

## 선택한 경계

평가 후보는 제품 Java 코드와 신규 테스트를 실행하고 Testcontainers로 Docker를 제어한다.
현재 개인 Docker endpoint는 `unix:///Users/jinhyeon/.docker/run/docker.sock`이다.
이 socket을 그대로 넘기거나, 명령 allowlist만 두는 것으로 평가 격리가 성립하지 않는다.

우선 Docker Sandboxes의 **로컬 mountless VM + VM 자체 Docker 엔진**을 검증한다.
공식 문서는 각 VM에 별도 커널·엔진을 두고 호스트 proxy에서 통신 정책을 집행한다고 설명한다.
이는 도구 선택 근거다. 기본 점검과 workload 증거를 포함해도 실제 에이전트 경계 전체를 입증한 것은 아니다.
[Docker 격리 문서](https://docs.docker.com/ai/sandboxes/security/isolation/)

`sbx`는 기존 Docker Desktop과 별도 CLI다. 공식 0.50.1 archive를 작업 전용 임시 폴더에
받아 공개 SHA-256과 macOS 코드 서명을 확인했다. 전역 설치·업데이트는 하지 않았다.
코드 서명 검사는 제한 셸에서 실패했으나 읽기 전용 권한 검토 후 동일 파일 검사에 성공했다.
macOS 15.7.7/arm64는 문서의 플랫폼 조건에 해당한다.
[설치 안내](https://docs.docker.com/ai/sandboxes/install/),
[고정 릴리스](https://github.com/docker/sbx-releases/releases/tag/v0.50.1)

## 모델 없이 먼저 확인할 순서

1. 사용자가 Docker 로그인으로 본인 인증을 완료한다(완료). Docker 로그인은
   기존 ChatGPT 로그인과 별개다. 클라우드 VM이나 추가 모델 실행은 필요하지 않다.
2. 소스 경로 없이 `shell` VM을 만든다. 공유 스킬을 끄고, 생성 시 이 VM에만 명시적
   network deny를 둔다. 개인 설정의 전역 allow/reset은 사용하지 않는다.
3. VM/daemon ID, 실제 mount, 스킬·MCP inventory, network policy를 호스트에서 보존한다.
   VM 내부 root가 호스트 socket·파일·개인 스킬·다른 평가 VM을 볼 수 없는지 확인한다.
   호스트에 합성 canary 파일/임시 loopback 서버를 만들어 도달 불가를 실제 검사한다.
   외부 통신은 domain, 직접 IPv4/IPv6, host alias, proxy 우회 경로를 각각 점검한다.
4. 플랫폼별 Java 21/Node/Git/Gradle와 이미지·의존성을 별도 준비한다. 원본 checkout의
   전체 mount/clone을 사용하지 않는다. 평가 입력 allowlist의 파일만 복사하고 파일 hash를 고정한다.
   macOS 캐시·설정이 Linux VM에서도 같다고 가정하지 않는다. 준비에 쓴 네트워크 허용은
   후보 실행 전에 닫고 경계 검사를 반복한다.
5. 정상 기준선과 T04/T05 회귀 사본으로 아래 행렬을 실행한다. 각 사본의 원본 테스트,
   CLI와 dependency cache를 고정하고 fresh XML·제품 diff hash·container cleanup을 보존한다.
6. VM 삭제 후 잔존 자원을 확인한다. 에이전트 실행용 인증 proxy와 Codex 실효 설정·
   스킬 inventory까지 검증하기 전에는 실제 비교를 시작하지 않는다.

공유 스킬은 기본으로 mount되므로 `--skills=off`가 필요하다. mountless여도 별도로
설정한 호스트 자원이 공유될 수 있다. 로컬 stdio MCP는 호스트에서 실행될 수 있으므로
빈 inventory 확인이 필요하다.
[공유 스킬 안내](https://docs.docker.com/ai/sandboxes/workflows/agent-skills/),
[격리 범위](https://docs.docker.com/ai/sandboxes/security/isolation/)

정책의 `deny-all` preset만으로는 kit가 추가한 allow를 막지 못한다. 준비용 shell VM에는
명시적인 VM별 deny를 사용하고 적용된 정책과 실제 연결 결과를 함께 확인한다.
[로컬 정책 및 우선순위](https://docs.docker.com/ai/sandboxes/governance/access-controls/local/)

| 사본 | unit 기대값 | integration 기대값 | 현재 VM 증거 |
| --- | --- | --- | --- |
| 정상 고정 기준선 | 21통과 | 31통과 | 21/31통과 |
| T04 회귀 | 21통과 | 삭제 후 계산/저장 학점 정합성 oracle 실패 | unit21통과, integration30통과·지정1실패 |
| T05 회귀 | 21통과 | 쓰기 transaction의 개인 cache 우회 oracle 실패 | unit21통과, integration30통과·지정1실패 |
| 두 회귀 원상복구 | 21통과 | 31통과 | 21/31통과 |

정확한 oracle은 [tasks.json](../tools/agent-harness/eval/tasks.json)을 사용한다.
단위 통과가 T04/T05의 성공 판정이 되는 것을 막는 행렬이다. 예상 밖 실패·skip·환경 오류는
준비 실패로 남기며 조건이나 테스트를 완화하지 않는다.

## 완료로 표시하지 않은 항목

- 실제 OAuth의 모델 경로 인증·갱신 검증(지정 auth/config 필드와 사용량 GET 수용만 확인)
- 실제 비교용 프로필의 새 회귀 VM workload 재검증(OAuth 프로필의 메타데이터만 확인)
- 작업용 빈 agent 환경에서의 SSH 중계 경계와 부정 HTTPS403의 차단 주체 확인
- provider 경로를 열어도 호스트/다른 run/MCP 접근이 차단되는지 검증
- 조건별 동일한 입력·도구·캐시, 시스템/개인 스킬 제외와 mju-verify만의 차이 확인
- 추가 모델 실행 승인 및 본 비교의 사람 채점

완료한 command/exec·MCP/스킬 inventory·VM 간 카나리·합성 token 치환·timeout VM 정리는
실제 서버 인증·갱신과 새 평가 run의 입력 고정을 대신하지 않는다. OAuth 초기 점검은 별도 빈 VM에서 수행했다.

도구 설치나 기본 점검만으로 위 항목을 완료 처리하지 않는다. 현재 승인된 모델 실행 잔여 횟수는 0이며,
이 준비는 추가 모델 호출을 포함하지 않는다. 실행 명령은 CLI README가 관리한다.
