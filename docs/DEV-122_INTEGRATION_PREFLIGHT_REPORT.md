# DEV-122 — 격리 VM의 T04/T05 사전 검증

2026-11-02에 고정 기준선과 합성 회귀의 **모델 없는 workload 검증**을 완료했다.
원시 증거는 `build/agent-harness/dev122-vm-workload-2c15mchs/`, 최종 대조는
`final-audit.json`, 실제 suite 기록은 `matrix-utf8/`에 있다.
이 결과는 Skill 효과나 실제 Codex 에이전트 평가 결과가 아니다.

## 확인한 동작

| 제품 코드 상태 | unit | 실제 합성 MySQL/Redis integration |
| --- | --- | --- |
| 정상 기준선 | 21통과 | 31통과 |
| T04 삭제 후 학점 재계산 제거 | 21통과 | 30통과·지정 oracle 1실패 |
| T05 쓰기 transaction의 cache 우회 제거 | 21통과 | 30통과·지정 oracle 1실패 |
| 두 회귀를 원상복구 | 21통과 | 31통과 |

모든 행에서 error/skip은 0이다. T04는 저장 학점이 `3.0`이어야 하는데 `6.0`으로 남았다.
T05는 쓰기 transaction에서 Redis를 읽어 `Unsafe transaction read Redis` 단언이 실패했다.
두 실패는 [과제 정의](../tools/agent-harness/eval/tasks.json)의 oracle과 일치한다.
단위 테스트만으로 이 두 회귀의 해결을 판정할 수 없다는 점을 새 VM에서도 확인했다.

## 입력과 실행 환경

- 소스 기준선: `b579ff46f3571c487415a004434093cf15a7dd67`. 허용한 tracked 경로를 export했고
  원본 checkout·학생 스냅샷·Git 이력·개인 설정을 mount하지 않았다. 테스트용 새 Git root는 정상 코드다.
- 입력은 소스·원본 테스트·CLI와 개발 의존성을 합쳐 909파일이다. CLI dist/node_modules
  266파일은 v2 파일럿의 지문과 일치하며 추가·누락 파일도 없다. 전체 읽기 가능한 입력을 별도 해싱했다.
- 의존성 전용 Gradle 캐시 3,709파일을 복사했다. v2 이후 `module-metadata.bin`과
  `modules-2.lock` 2파일의 내용이 바뀌어 새 hash를 고정했다. 이전 Mac 파일럿과 동일한 환경이라고 주장하지 않는다.
- 로컬 mountless shell VM, Linux arm64, 2 CPU/4 GiB, Node 22.22.1, Git 2.53.0,
  [Temurin JDK 21.0.13+9](https://github.com/adoptium/temurin21-binaries/releases/tag/jdk-21.0.13%2B9).
  JDK archive SHA-256은 `e3522e707fc7dbe2a72c30746b1e7068301a0213ecd213d4491f35a14eb55578`이다.
- `LANG=LC_ALL=C.utf8`, 별도 `GRADLE_USER_HOME`, VM의 Unix Docker socket을 사용했다.
  실행한 명령은 각 상태에서 `verify takenlecture-unit --offline`과
  `verify takenlecture-integration --offline`이다. CLI·Gradle·기존 테스트는 수정하지 않았다.

공개 이미지 `mysql:8.4.8`, `redis:7.4-alpine`, `testcontainers/ryuk:0.13.0`은 호스트에서
파일로 export한 뒤 VM에서 checksum을 대조해 load했다. VM의 network deny는 유지했다.
호스트는 config digest, VM은 OCI manifest digest를 이미지 ID로 반환했다. 단순 ID 문자열
대조 대신 **archive manifest → 원본 config SHA → guest config/RootFS layers**를 비교했다.
작은 원본 manifest/config JSON 6개와 각 지문을 증거에 보존해 독립 재검사가 가능하다.

## 실패 보존과 최종 검토

첫 정상 unit 시도는 한글 inner-class 파일명 컴파일에서 멈췄고 XML은 생성되지 않았다.
이를 테스트 회귀 실패로 세지 않는다. VM의 지원 locale을 확인한 뒤 C.utf8을 고정했고
Java의 `file.encoding`, `native.encoding`, `sun.jnu.encoding`이 모두 UTF-8임을 확인했다.
최초 실패 report/Gradle 로그는 상위 증거 폴더, 재실행 8행은 `matrix-utf8/`에 각각 보존했다.

독립 리뷰는 정리 확인에서 종료 코드 없이 빈 stdout만 성공으로 처리하는 감독기 결함을 발견했다.
실제 실행본을 보존한 후 후속 스크립트에 조회/정지 종료 코드 검사를 추가했다. 이번 실행은
별도 최종 감사에서 원시 종료 코드·새 전체 자원 조회·VM stopped 상태를 대조해 보완했다.
후속 스크립트는 정적 검토만 했으며, 테스트 행렬을 추가 실행한 것으로 세지 않는다.

최종 감사와 독립 재리뷰에서 다음을 확인했다.

- 8행의 fresh XML·CLI 결과·실행 장부·정의된 회귀 diff 지문 일치
- 원본 테스트 198파일(190 Java, 기타 8파일)과 복구된 제품 2파일의 SHA 일치
- 전체 임시 container·volume·custom network 각 0, VM 정지 종료 코드 0과 stopped 상태
- 제품 저장소의 Java·기존 테스트·검증 CLI·스킬 변경 없음, 추가 모델 실행 0회

## 남은 범위

이 VM은 정상 Git 이력·컴파일 결과·실패/성공 로그를 보유하므로 **실제 평가 에이전트에
재사용하지 않는다**. 실제 run마다 정답이 없는 회귀 root와 의존성만 받은 새 VM이 필요하다.
현재 증거는 VM 안의 CLI/DB workload가 동작한다는 뜻이다. Codex 명령 API, 다른 run 접근,
MCP inventory, 인증 proxy·ChatGPT 로그인 호환성과 후보의 권한 경계는 아직 검증하지 않았다.

HTTP·UI를 이번에 다시 실행하지 않았다. 사람 채점과 본 비교 30회도 미실행이며,
추가 모델 실행의 잔여 승인은 0회다. 별도 모델 실행 전 실제 에이전트 경계를 준비하고
조건별 입력·이미지·캐시를 다시 고정해야 한다. 커밋·푸시·배포는 수행하지 않았다.
