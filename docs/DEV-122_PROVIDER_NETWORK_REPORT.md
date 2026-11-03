# DEV-122 — ChatGPT provider와 선택 네트워크 점검

후속 결과: [T04 제한 파일럿 준비](DEV-122_T04_PILOT_PREPARATION_REPORT.md)에서 SSH 중계와 HTTPS 차단 주체를
확인하고 새 두 회귀 VM을 고정했다. 아래는 그 전 단계의 관찰이며 갱신 endpoint401은 여전히 미검증이다.

2026-11-03에 제품 소스 없는 로컬 VM에서 평가용 설정과 네트워크를 점검했다.
**사용량 GET 1회는 서버에 수용됐고 모델 추론은 0회다. 전체 평가 준비는 미완료다.**
원시 증거 128파일은 `build/agent-harness/dev122-provider-lyrefgpp/`, 해시 목록은
그 안의 `preservation.json`, 요약은 [결과 JSON](../tools/agent-harness/eval/provider-network-result.json)이다.

## 확인한 결과

| 대상 | 관찰과 범위 |
| --- | --- |
| 기본 kit의 정책 함정 | plain network allow와 좁은 HTTP allow를 함께 두면 다른 경로·메서드도 200. 로컬 sink가 실제 3요청을 받음 |
| 전용 kit | 기본 kit를 상속하지 않음. 동일 이미지 digest 고정, shell 진입점, OpenAI OAuth만 요청, plain network allow 0 |
| OAuth 바인딩 | 기존 계정 연결 승인과 진행 요청 범위에서 공식 CLI로 auth.openai.com/chatgpt.com만 바인딩. 새 로그인·API 키 없음 |
| 평가 설정 | 별도 OAuth 프로필을 고정 Codex 0.166.0이 읽음. ChatGPT backend·공개 sentinel·Responses 형식 확인 |
| Codex 메타데이터 | RPC5개, MCP0, 활성 스킬0, account=null/requiresOpenaiAuth=false. 모델·명령 시작 RPC 없음 |
| 로컬 HTTP 정책 | 합성 Responses POST와 usage GET만 도착. 다른 메서드·경로·포트 4건은 403이고 sink 도착 0 |
| proxy 우회 | 직접 loopback/domain/IP 연결 3건 실패. 지정한 호스트 checkout·개인 Docker socket 경로 보이지 않음 |
| 서버 인증 요청 | TLS 검증·redirect/retry 없이 usage GET 1회. HTTP200, JSON content-type과 예상 rate-limit 필드의 형식 확인 |
| 부정 HTTPS 요청 | 3건 모두 403 관찰. proxy와 원본 서버 중 차단 주체는 확인하지 않았으므로 HTTPS 경계 통과로 확대하지 않음 |
| 새 kit의 Docker | private engine 사용 가능, 호스트 engine ID와 상이. 이번에 Java 21/DB workload를 실행한 것은 아님 |
| 최종 정리 | 임시 VM2개 삭제, 기존 workload VM 정지. deny3개 복원·HTTP allow 제거 후 삭제, 합성 서버·SSH socket 정리 |

첫 정책 재현은 `policy-probe/`, 전용 kit의 성공 증거는 `restricted-network-2/`,
전체 프로필은 `full-profile-metadata/`, 실제 GET은 `provider-get-2/`에 있다.
CIDR 전체 deny를 유지한 첫 로컬 시도는 허용 요청도 500으로 끝나 `restricted-network/`에 남겼다.
서버 GET 전에는 그 실패에서 남은 HTTP allow를 감지해 중단했고, 해당 VM의 규칙을 ID로
모두 제거한 뒤 allow가 없음을 확인했다. 실제 GET 최대 1회 표식도 보존했다.

## 인증과 데이터 범위

[Docker 정책 문서](https://docs.docker.com/ai/sandboxes/governance/concepts/#combine-http-and-network-rules)를
근거로 선택한 규칙을 실제 로컬 요청으로 대조했다. HTTP canary 통과는 HTTPS 전체의 증거가 아니다.
기본 kit의 넓은 allow 위에 HTTP allow만 추가하는 구성은 평가 후보에서 제외했다.

[OpenAI provider 설정 문서](https://learn.chatgpt.com/docs/config-file/config-advanced#custom-model-providers)와
[Docker의 공개 OAuth sentinel 설정](https://hub.docker.com/layers/docker/sbx-kit-codex-mixin/latest/images/sha256:be9a4a6c62e9bbfe4c2dfee47484f8d2ae9bdc1ae7a0c3ebad3bef8570c2f9c8/sbx-kit-spec)을
참고했다. 실제 사용량 URL은 [고정 Codex 소스](https://github.com/openai/codex/blob/rust-v0.166.0/codex-rs/backend-client/src/client/rate_limit_resets.rs)의 GET 경로를 확인했다.
HTTP200만으로 판정하지 않고 plan_type/rate_limit 및 내부 boolean 필드 형식을 검사했다.
응답 본문·헤더·계정 ID·계획 값·사용량 값·실제 토큰은 보존하지 않았다.
curlrc를 비활성화했고 curl body 제한을 설정했으며 Python의 출력 읽기도 66,048바이트로 제한했다.
사용량 응답 판정의 합성 검사 8건이 통과했다.

이 결과는 해당 읽기 요청이 수용됐다는 증거다. 사용자 신원·토큰 갱신·Responses 권한·
선택 모델의 실행 가능 여부를 증명하지 않는다. 실제 provider의 Responses POST는 허용하거나 실행하지 않았다.

## SSH와 남은 조건

기본 SSH forwarding 설정과 VM socket 존재를 관찰했다. 전역 설정은 바꾸지 않고 작업용
[빈 SSH agent](../tools/agent-harness/eval/empty_ssh_agent.py)를 host sbx 호출에 전달했다.
이 helper는 키를 저장하거나 개인 agent에 접속하지 않고 빈 목록/실패만 반환한다.
호스트 합성 서버에 identity 요청이 도착했지만 VM의 중계 조회는 EOF로 끝났고 서명 요청은 실행되지 않았다.
따라서 VM의 SSH 중계 경계는 아직 통과로 표시하지 않는다. raw cleanup의 개인 agent 미접촉 표기는
helper 구현 범위이며 Docker daemon의 모든 fallback을 감사했다는 의미가 아니다.

독립 코드 검토에서 느린 전송으로 helper 종료가 지연되는 문제를 찾아 전체 읽기 deadline과
finally 정리를 추가했다. 해당 회귀를 포함한 Python21검사가 통과했다. 제한 셸의 socket bind
3오류는 환경 실패로 구분했으며 제품 테스트 실패로 집계하지 않는다.
마지막 test-only 실패 정리 보완 뒤에도 메인 에이전트가 최종 21검사를 통과했다.
독립 읽기 검토에서 128파일 해시·GET·로컬 정책 수치·정리·한계가 일치했고 추가 지적은 없었다.
이 검토는 실제 비교의 사람 채점이 아니다.

SSH 중계와 부정 HTTPS 차단 주체·갱신 확인, 새 회귀 root VM의 입력/Java21/cache/image 준비와
실제 명령 경로 재검증이 남아 있다. 기존 모델 실행 잔여 승인은 0회이며 실제 비교 전에 별도
전송·횟수 승인이 필요하다. 사람 채점·Skill 효과·Spring↔브라우저 E2E도 이번 증거에 포함하지 않는다.
