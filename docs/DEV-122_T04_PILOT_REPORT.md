# DEV-122 — T04 통합 파일럿 결과

2026-11-03. 추가 승인한 **2 agent 실행과 독립 원본 oracle 채점을 완료했다.**
두 조건 모두 단위21·통합31을 통과했고 최종 제품 코드도 같았다.
평가용 회귀 사본의 결과이며, Skill 우위나 전체 평가 완료를 뜻하지 않는다.

[결과 JSON](../tools/agent-harness/eval/integration-pilot-result.json),
[고정 조건](../tools/agent-harness/eval/integration-pilot-lock.json),
[준비 보고서](DEV-122_T04_PILOT_PREPARATION_REPORT.md)에 실행 범위와 근거를 연결했다.
원시 trace·후보 바이트·보고서/JUnit·외부 채점·사람 리뷰 자료는
`build/agent-harness/dev122-t04-pilot-v1/`에 보존한다.

## 관찰 결과

| 조건 | Codex 실행 시간 | 후보의 최종 검증 | 독립 원본 테스트 | 금지 파일 변경 |
| --- | --- | --- | --- | --- |
| without-skill | 240.347초 | unit21 / integration31 통과 | unit21 / integration31 통과 | 0 |
| with-skill | 249.006초 | unit21 / integration31 통과 | unit21 / integration31 통과 | 0 |

각 suite의 실패·오류·skip은0이다. 실행 시간은 guest launcher의 Codex 실행/종료 경과이며
환경 준비·독립 채점은 제외한다. 호스트 감독 경과는 각각245.097/253.608초,
외부 검증 경과는 각각63.011/63.283초다. 이 한 쌍의 시간 차이를 Skill 효과로 해석하지 않는다.

- 두 조건 모두 변경 전 통합31개 중 의도한 학점 회귀1개를 재현하고, 수정 후 필수 suite를 통과했다.
- 제품 변경은 삭제 후 `generateOrModifyCompletedCredit(user)` 호출을 복구한 같은 바이트다.
  source SHA256은 `153882859830393c58f862328b03389b916173566654ee7245ba555dfefecde2`,
  제품 diff SHA256은 `7bddb04ece162968e2eb0eb731df7ad2923fecf39c161deb250058008b7ce2cc`다.
- 각 조건은 허용 제품 파일1개와 새 회귀 테스트1개만 변경했다. 기존 테스트·CLI·설정 변경은0이다.
- with 조건에서 mju-verify 파일을 읽은 성공 명령을 관찰했다. without 조건에서는 관찰하지 않았다.
  읽기 관찰은 효과 판정이 아니다.
- 후보 검증 증거는16/18개이며, 최종 제품 diff와 일치하는 unit/integration 보고서가 각각 있다.
  선택 범위 밖의 HTTP doctor는 without 조건에서 필요한 이미지가 없어 실패했다. HTTP suite와 UI는 실행하지 않았다.

## 독립 채점과 실행 경계

독립 채점은 정상 코드에 후보 diff를 얹는 대신 **원래 T04 회귀 root**에서 시작했다.
후보의 허용 제품 파일 바이트만 교체하고 원본 mode·테스트·CLI·설정을 유지했다.
새 테스트는 외부 oracle에 포함하지 않았다. 두 suite의 코드 지문과 저장한 제품 diff가 일치하고,
실행 전후 제품/테스트 입력이 변하지 않았으며 fresh JUnit의 개별 결과와 집계도 대조했다.

채점 VM은 각각 새 private Docker engine을 사용했고 평가 VM의 engine과 달랐다.
Java/cache 전체 inventory, config/runtime/image hash와 image RootFS를 확인했다.
scoped deny3개, host checkout/socket 비노출을 확인했으며 채점 VM2개는 삭제했다.
유효 JUnit 비통과와 실행/증거 환경 오류를 구분하는 채점기는 독립 정적 검토를 거쳤다.
이번 두 채점은 정상 통과했으며 추가 모델 호출은 없었다.

평가 VM2개는 정지했고 network deny3개·HTTP/network allow0으로 돌아왔다.
정리 확인의 첫 시도는 긴 UNIX socket 경로 때문에 VM 접근 전에 실패했다.
짧은 임시 socket 경로로 읽기 확인만 다시 실행해 정지·정책·채점 VM 부재를 확인했다.
이 실패는 모델 재시도가 아니며 별도 증거로 남겼다.

## 승인·사용량·남은 판정

기존 승인4회 뒤의 추가2회 승인에 대한 사용자 `진행` 요청을 기록하고,
동일 gpt-6-astra/high·Codex0.166.0·프롬프트/설정·without→with 순서를 유지했다.
각 최대1200초, 외부 agent 재시도0이며 추가 승인 잔여0이다.
agent1회는 여러 API 요청을 포함한다. 평가 사본과 보존 증거에 인증 토큰 원문·학생 스냅샷·실제 DB 레코드는 포함하지 않았다.

| 관찰 token 항목 | without | with |
| --- | --- | --- |
| input | 783,849 | 780,002 |
| cached input | 746,368 | 738,560 |
| output | 7,703 | 7,517 |
| reasoning output | 1,275 | 1,589 |

위는 CLI가 제공한 누계 그대로다. 실제 금액과 provider API 요청 수는 미제공이므로 null이다.
실제 provider 추론 성공은 확인했으나 토큰 갱신·호스트 회전은 여전히 미검증이다.
전체30회 비교와 다른 사용자 경로 검증은 수행하지 않아 `fullEvaluationReady=false`를 유지한다.

`human-review/`에는 조건명·순서·시간·사용량·Skill 읽기 과정을 제외한 두 후보 자료와
빈 scores.json을 준비했다. 코드/테스트/답변으로 조건을 추론할 수 있어 완전한 눈가림은 아니다.
일부 사용자 지정 로그는 고정 수집 패턴 밖이어서 명령/출력 trace로 보완한다.
원본 suite 증거와 신규 테스트 자체의 충분성을 구분해 리뷰한다.
`taskPassed`, `verificationScore`, `falseCompletion`, `scopeOverclaim`과 Skill 효과는 사람이 판정하기 전까지 null이다.
다음은 이 자료의 사람 채점과 본 비교30회 조건 확정이며, 추가 모델 실행은 별도 승인 범위다.
