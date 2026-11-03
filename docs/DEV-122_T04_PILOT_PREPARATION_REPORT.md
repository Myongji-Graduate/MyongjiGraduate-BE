# DEV-122 — T04 제한 파일럿 준비

후속: [T04 실행 결과](DEV-122_T04_PILOT_REPORT.md)에서 새 승인2회 실행과 독립 원본 oracle의
unit21/integration31 통과를 확인했다. 아래는 실행 전 고정한 준비 스냅샷이다.

2026-11-03. 새 T04 비교 VM 두 개를 고정했다. **이번 준비에서 모델 실행과 모델로의
소스 전송은 0회다.** 기존 v1/v2 파일럿의 승인 4회는 모두 사용했으며 새 승인을 기다린다.
전체 30회 비교와 Skill 효과 판정은 완료하지 않았다.

요약은 [준비 결과](../tools/agent-harness/eval/integration-pilot-preparation-result.json),
구체 조건은 [실행 잠금](../tools/agent-harness/eval/integration-pilot-lock.json)에 있다.
원시 증거는 `build/agent-harness/dev122-gates-ea9pr5ci/`,
`build/agent-harness/dev122-vm-job-oyffaymg/`,
`build/agent-harness/dev122-integration-pilot-o9ljg88z/`에 해시 목록과 함께 보존한다.
큰 런타임 tar와 중복 소스 트리는 복제하지 않았다. 활성 입력과 VM은 로컬에 유지한다.

## 닫힌 경계와 남은 인증 한계

| 항목 | 관찰과 범위 |
| --- | --- |
| SSH 중계 | VM에서 identity 0개, 같은 연결의 idle 후 재조회 성공, 합성 서명 거절. 개인 SSH agent에 연결하지 않음 |
| HTTPS 부정 요청 | 3개 모두 403. 제어 가능한 로컬 차단 응답의 정규화 지문과 일치하며 허용하지 않은 요청의 카나리 도착 0 |
| 사용량 GET | 앞 단계에서 HTTP200과 예상 JSON schema 확인. 계정·응답 원문은 저장하지 않음 |
| 갱신 endpoint | 공개 sentinel만 보낸 form 1회와 JSON 1회가 각각 401. 실제 토큰 회전·갱신 대체 값 미확인 |
| 제한 파일럿 | 격리 점검을 통과한 2회 실행만 제안. 인증 사전 점검 또는 실행 환경 오류 시 비교 중단, 외부 재시도 0 |

HTTPS 지문 확인의 후속 시도에서는 URL/host/port 정규화와 `--compressed`를 함께 추가했다.
실제 응답의 gzip magic은 없었다. 최초 지문 불일치의 원인을 압축으로 단정하지 않는다.
호스트 합성 agent에서 관찰한 요청은 identity 유형 11뿐이다. 서명 거절은 VM 중계에서
확인했으며 호스트 helper가 서명 유형 13을 받았다고 주장하지 않는다.

갱신 401은 해당 경로의 미검증 결과다. 계정 전체가 잘못됐다는 의미로 해석하지 않는다.
원문 토큰 추출·새 로그인·네트워크 확대는 수행하지 않았다.
[Docker credential 명세](https://github.com/docker/sandbox-kit-spec/blob/main/docs/spec/capabilities/com.docker.sandbox/credential@1.md)의
호스트 갱신/대체 값 설계와
[고정 Codex의 JSON 갱신 구현](https://github.com/openai/codex/blob/rust-v0.166.0/codex-rs/login/src/oauth/client.rs)을
확인했으나 실제 호스트 회전의 증거는 없다. `refreshVerified=false`, `fullEvaluationReady=false`다.

## T04 입력과 모델 없는 회귀 탐지

기준선은 `b579ff46f3571c487415a004434093cf15a7dd67`, 과제는 삭제 후 학점 정합성 T04다.
건강한 이력의 기존 workload VM을 재사용하지 않고 새 VM에 회귀 root 한 커밋만 넣었다.
부모 커밋·remote·alternates·unreachable object가 없고 정상 답안 이력을 VM에 복사하지 않았다.

두 조건의 파일 차이는 `AGENTS.md`, `mju-verify/SKILL.md`, unit/integration 지도 2개뿐이다.
소스·원본 테스트·CLI·프롬프트와 나머지 파일의 hash/type/mode가 일치한다.
Darwin/Linux의 Node 실행 symlink mode 차이는 별도 기록하고 link target 및 실제 파일을 비교했다.

| 조건 | 단위 | 합성 MySQL/Redis 통합 | 활성 Skill / MCP |
| --- | --- | --- | --- |
| without-skill | 21통과 | 31개 중 예상 실패 1, 오류/skip 0 | 0 / 0 |
| with-skill | 21통과 | 31개 중 예상 실패 1, 오류/skip 0 | 1 / 0 |

실패는 두 조건 모두 `sequentialCommitAndDeleteKeepActualCalculationAndSavedCreditsAligned()`의
expected 3.0 / actual 6.0이다. 이는 과제의 회귀 탐지 증거이며 에이전트의 수정 성공이나 Skill 효과가 아니다.

Java 21.0.13+9, Codex 0.166.0 Linux arm64, 의존성 캐시 3,709파일과 이미지 3개를 재고정했다.
Docker의 guest manifest ID와 host config ID는 직접 같은 값으로 비교하지 않고,
원본 JSON hash·manifest의 config digest·실제 RootFS layer를 연결해 확인했다.
검사 후 결과·workspace Gradle 상태·Codex 세션을 지우고 캐시를 원본과 다시 비교했다.
최종 두 VM은 컨테이너/볼륨/custom network 0이며 정지 상태다.

초기 준비의 symlink mode 비교 중단, 잔존 Ryuk 감지, image ID 비교 중단도 보존했다.
without 조건의 Ryuk은 stop/restart의 auto-remove 후 사라졌고 별도 `docker rm` 결과는 없다.
with 조건에서는 소유한 Ryuk 1개를 명시적으로 제거했다. 이 준비 실패를 모델 시도로 세지 않는다.

## 실행 감독과 제안된 흐름

[vm_job.py](../tools/agent-harness/eval/vm_job.py)는 독립 watchdog이 sbx 실행 시작부터 소유한다.
클라이언트 그룹을 종료한 뒤 VM 정지·deny 복원·상태 확인을 마치고 결과를 기록한다.
종료된 leader를 reap하기 전에 같은 그룹의 자손도 제거한다.

- Python helper/채점/감독 회귀 검사 29개 통과.
- 소스 없는 실제 VM의 정상 종료, timeout, owner SIGKILL, 지연된 실행 시작 전 owner 종료,
  leader 종료 후 남은 host 자손의 5개 시험 통과. 모델 호출 0회.
- 사용자별 `/tmp/dev122-vm-leases-<uid>`에서 이름 lease를 잡는다. 서로 다른 TMPDIR의
  별도 프로세스도 같은 lease를 사용함을 확인했다.

모든 하네스 호출자는 동일한 lease를 prolog → 실행 → 수집 → 정지까지 유지한다.
이것은 협력하는 하네스 간 예약이며, 외부 CLI/UI의 동시 삭제·이름 교체를 원자적으로 막지는 않는다.
UUID가 달라지면 다른 VM을 정리하지 않고 중단한다.

보존된 `run-approved-t04.py`의 기본 동작은 host hash·VM UUID/정지·deny 정책 검사다.
`--execute`는 별도 사용자 승인 기록과 정확한 lock hash가 필요하며, 승인 없는 실행은 VM 접근 전에 거부된다.
실제 승인 실행에서는 두 VM의 같은 lease descriptor를 watchdog에 전달한다.

독립 리뷰에서 launcher의 proxy 환경 누락과 수집 범위 부족을 찾아 수정했다.
인증정보 없는 Docker 고정 proxy URL과 고정 CA만 전달하고 API/GH/SSH 환경은 제외한다.
두 VM에서 실제 launcher와 같은 환경 빌더 및 curl 자동 proxy 선택으로 TLS 검증을 켠 부정 GET이
정책403을 반환했다. 이 점검의 사용량 GET·Responses POST·모델 호출은0회다.
수집기는 최종 workspace hash/type/mode, src 전체와 새 테스트, 기존 테스트·설정·CLI 바이트,
CLI report/JUnit/Gradle log를 크기 제한과 함께 보존한다. 금지 파일 변경 여부와 원본 oracle 채점을 분리한다.
합성 fixture로 이 수집 범위와 symlink 거부/누락 기록, 잘못된 proxy/CA 거부를 확인했다.

승인 후 순서는 without-skill → with-skill, 각 최대 1,200초, 총 2 agent 실행이다.
각 agent 실행 안에서는 여러 API 요청이 생길 수 있다. 실행 전에는 schema만 남기는 사용량 GET 1회,
실행 중에는 ChatGPT Responses의 정확한 POST 경로만 허용한다. 종료 후 deny 복원과 VM 정지,
차단 상태에서 후보 제품 바이트 수집, 재정지 순서다. 첫 인증/환경 오류에서 두 조건 비교를 중단한다.

[고정 provider 구현](https://github.com/openai/codex/blob/rust-v0.166.0/codex-rs/model-provider-info/src/lib.rs)의
custom provider WebSocket 기본값이 false임을 확인했다. 실제 Responses 실행 가능 여부는 아직 미검증이다.
외부 재시도·조건 사이 설정 변경은 하지 않는다. 독립적인 새 합성 grade VM에서 후보 제품 변경만
원본 테스트/CLI로 채점할 예정이며, 사람의 정성 점수는 아직 null이다.
