# DEV-122 — Codex 명령·MCP·인증 경계 검증

2026-11-02에 **평가용 모델 추론 없이** Codex 명령 경로와 VM 경계를 점검했다.
원시 증거는 `build/agent-harness/dev122-agent-boundary-ivhukbb5/`, 상태 요약은
[결과 JSON](../tools/agent-harness/eval/codex-vm-boundary-result.json)에 있다.
2026-11-03에는 사용자 승인으로 Docker OpenAI OAuth를 등록하고 소스 없는 VM의 초기 대체 값을 확인했다.
해당 증거는 `build/agent-harness/dev122-oauth-qajxrrj5/`, 요약은
[OAuth 결과 JSON](../tools/agent-harness/eval/codex-vm-oauth-result.json)이다. 실제 서버 인증·갱신·새 모델 비교는 미실행이다.

## 최종 관찰값

| 점검 | 결과와 근거 |
| --- | --- |
| 고정 클라이언트 | 공식 npm Codex `0.166.0-linux-arm64`; archive SHA-512 및 host/guest archive·binary SHA-256 일치 |
| 명령 경로 | app-server `command/exec` → `/bin/bash -c` → 실제 CLI; unit 21통과·integration 31통과 |
| 설정 | gpt-6-astra/high 문자열 유지, login shell·web·앱·플러그인·기억·추가 에이전트 비활성화 확인 |
| MCP | 실제 Codex inventory 0, Docker 등록 inventory 0 |
| 스킬 발견 | 미제공 0, 고정 mju-verify 제공 1; 시스템 스킬 제외 |
| 인증 변수(연결 전 명령 검사) | 허용한 환경만 자식 프로세스에 전달; account 없음, 명령에 API/GitHub/MCP 인증 변수 없음 |
| 합성 proxy | VM은 placeholder 사용, 호스트 테스트 서버는 치환된 합성 헤더 관찰; 실제 OpenAI 인증 사용 없음 |
| VM 간 경계 | root에서도 호스트·다른 VM의 지정 파일과 카나리 서버 접근 실패, Docker 엔진 ID 상이 |
| timeout 정리 | Codex 명령 1.5초 timeout/exit124 이후 살아 있던 setsid 자손이 VM 정지·재시작 후 종료 |
| 최종 자원 | workload VM 정지, 빈 proxy VM 삭제, 임시 인증·호스트 resolver·카나리 제거 |

최종 명령 증거는 `command-final/`, JUnit은 `final-takenlecture-*-evidence/`다.
실행한 검사기는 `check-vm-command-cleanup-reviewed.py`이며 현재 저장소 소스와 SHA가 같다.
`check-vm-command-final.py`는 이전 실행본으로 최종본이 아니다.

## 모델 호출을 막은 경로

검사기는 initialize/config/account/skills/MCP 목록과 command/exec만 허용한다.
thread/start·turn/start·로그인 시작 같은 메서드는 I/O 전에 거부한다. 최종 RPC 11개에
thread/turn 시작이 없고, 실제 생성 추론은 0회다. 모델 사용량·비용·수정 성공률을 측정한 결과가 아니다.
[OpenAI의 명령 API 문서](https://learn.chatgpt.com/docs/app-server#command-execution)는
command/exec가 thread 생성 없이 단일 명령을 실행하는 경로임을 설명한다.

[VM 전용 설정](../tools/agent-harness/eval/codex-vm-config.toml.template)은 VM 안에서만 사용한다.
내부 sandbox를 중복 적용하는 대신 API의 externalSandbox와 호스트가 관리하는 mountless VM·
network deny가 실행 경계다. 이 설정을 호스트의 개인 Codex 설정에 설치하지 않는다.

## 합성 인증과 실패 보존

실제 계정 대신 무작위 합성 값의 resolver를 호스트 임시 경로에 두고, 소스 없는 별도 VM에만
custom secret을 등록했다. kit의 외부 도메인을 먼저 deny한 후 로컬 canary 서버의 정확한
포트·GET 경로만 잠시 허용했다. VM root는 resolver 경로를 읽지 못했고, 알려진 인증 파일과
환경에서 합성 값이 검출되지 않았다. 서버는 헤더 일치 여부만 반환해 값 자체를 VM에 돌려주지 않았다.

`synthetic-proxy-3/`가 성공 증거다. 첫 두 시도는 비대화형 정책 삭제 옵션 누락과
host.docker.internal의 localhost 변환 규칙으로 실패해 각각 보존했다.
세 번째는 치환을 확인한 뒤 domain/IPv4/IPv6 deny 복원과 host cleanup까지 성공해야
통과로 기록한다. 임시 VM 삭제 후 해당 인증 scope가 비어 있음을 별도로 확인했다.
[Docker의 호스트 서비스 연결 규칙](https://docs.docker.com/ai/sandboxes/workflows/development/#accessing-host-services-from-a-sandbox)

첫 Codex 검사도 기본 VM의 인증용 환경변수 상속으로 시작 전에 중단했다. 부모 환경에서
필요한 항목만 허용하도록 보완했고, 중간 실행·최종 실행을 모두 보존했다. 독립 리뷰가 찾은
정리 전 성공 기록 문제는 중첩 finally와 성공 gate로 수정했다. 인증 변수 제외·실효 설정 변조·
모델 시작 금지·정리 실패 전파 등을 포함한 Python 15검사가 통과했다.

## 종료와 독립 검토

timeout 증거에서 명령 종료 후에도 자손 heartbeat가 18→21로 증가했다. VM 정지 후 재시작하자
자손과 독립 카나리 프로세스가 모두 없고 heartbeat가 24→24로 고정됐다. 이는 실행 그룹을
벗어난 자손도 호스트의 VM 정지로 종료할 수 있음을 확인한 합성 사례다.

독립 에이전트는 최종 코드·프로필 지문, 21/31 fresh XML, MCP·스킬 inventory, 합성 헤더,
deny 복원, timeout·정리 원시 결과를 대조했다. 정성 점수나 모델 평가를 수행하지 않았다.
전역 service/MCP 등록이 비어 있는 원시 inventory도 별도로 보존했다.

## 승인된 실제 계정 등록과 초기 대체 값 확인

기존 Codex 로그인 저장소와 Docker Sandboxes의 OpenAI 인증 저장소는 별도다.
이 버전의 `sbx secret set --help`는 OAuth를 **openai/global only**로 안내한다.
`sbx secret set openai --oauth`는 호스트의 브라우저 흐름과 macOS Keychain을 사용하며,
등록한 인증은 허용된 다른 로컬 Docker 샌드박스에서도 사용할 수 있다.
[Docker Codex 인증 안내](https://docs.docker.com/ai/sandboxes/agents/codex/#authentication),
[인증 저장·proxy 문서](https://docs.docker.com/ai/sandboxes/configuration/credentials/)

사용자가 이 범위를 명시적으로 승인했고 호스트 OAuth 등록이 종료 코드 0으로 완료됐다.
OpenAI 등록은 1개/global이며 유지한다. 제품 소스·공유 스킬·workspace mount 없이 만든
새 VM은 저장된 OAuth 사용을 보고했다. domain/IPv4/IPv6 deny는 삭제 시점까지 유지했다.

VM 내부에서만 auth.json이 API-key 모양의 고정 대체 값이고 provider bearer도 공개 sentinel과
정확히 일치하는지 검사했다. 실제 토큰은 호스트에서 추출하지 않았고 인증 파일을 호스트로 복사하지 않았다.
이 검사는 지정한 auth/config 필드에 한정하며 VM 전체의 모든 비밀 누출을 증명하지 않는다.
[Docker 공개 Codex kit 설정](https://hub.docker.com/layers/docker/sbx-kit-codex-mixin/latest/images/sha256:be9a4a6c62e9bbfe4c2dfee47484f8d2ae9bdc1ae7a0c3ebad3bef8570c2f9c8/sbx-kit-spec)은
OAuth에서도 forced API login과 ChatGPT backend provider·대체 값을 함께 사용하는 구조를 보여준다.
이 공개 설정은 대체 값의 출처이며 이번 VM의 kit image digest를 고정했다는 의미는 아니다.

고정 Codex 0.166.0의 별도 메타데이터 프로필에서 최종 RPC는 initialize/config/read/account/read/
mcpServerStatus/list/skills/list 5개뿐이다. MCP0·활성 스킬0이며 thread/turn·명령 실행은 없다.
account/read(refreshToken=false)는 account=null, requiresOpenaiAuth=false를 반환했다.
따라서 로컬 ChatGPT 사용자 신원이나 서버가 인증을 받아들였다는 증거로 해석하지 않는다.
서버 요청과 token refresh는 실행하지 않았다. 이 프로필은 실제 비교용으로 고정한 실행 프로필이 아니다.

처음 일반 대체 값을 예상한 준비 2회와 계정 유형을 예상한 RPC 3회는 중단돼 별도 보존했다.
미확인 provider 값을 조건부로 출력하는 검사는 자동 승인 검토가 거절해 실행되지 않았다.
이후 원문 없는 불리언 검사와 공개 sentinel 대조만 사용했다. 계정 응답의 원문·개인 필드도 저장하지 않았다.
최종 프로세스와 reader 종료를 확인한 뒤 임시 VM을 삭제했다. 보존 파일 40개의 hash를 확인했다.
기존 workload VM은 정지 상태이며 정상 이력과 결과를 포함하므로 실제 비교에 재사용하지 않는다.
독립 읽기 검토에서 40파일 hash·최종 RPC5개·정리·실패 보존과 보고서의 한계가 일치했고 추가 지적은 없었다.
이 검토는 본 비교의 사람 채점이 아니다.

실제 모델 실행에는 별도 전송·횟수 승인이 필요하며 현재 잔여 승인은 0회다. 각 평가 run의
새 회귀 root·입력/캐시 고정, ChatGPT provider 설정을 반영한 실행 프로필, 실제 OpenAI 경로만
허용한 네트워크 경계·서버 인증/갱신 검증과 사람 채점은 남아 있다.
이번에는 제품 Java·기존 테스트·CLI·mju-verify 및 커밋·푸시·배포를 변경하지 않았다.
