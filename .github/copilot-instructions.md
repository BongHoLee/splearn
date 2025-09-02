# AI Coding Assistant Guide for `splearn`

Concise, project-specific rules so you can contribute productively fast. Follow these – avoid generic boilerplate.
## 에이전트 상호작용 & TDD 작업 규칙
이 저장소에서 AI 에이전트(Copilot 등)가 코드를 변경/추가할 때 반드시 아래 순서를 따른다.

### 기본 응답 언어
- 모든 답변은 별도 요청이 없는 한 자연스럽고 간결한 한국어를 기본으로 한다.
- 영어/다국어가 필요한 경우 사용자 명시 요청시에만 전환.

### TDD 사이클 (Red → Green → Refactor)
1. 계획(Plan): 변경 요청을 받으면 먼저 짧은 한 문장 목적 + 체크리스트(명시적 요구사항 + 합리적 추론)를 제시.
2. Red: 실패해야 하는 최소 테스트 작성.
	- 테스트 파일/케이스 추가 또는 기존 확장.
	- 작성 후 즉시 테스트 실행.
	- 실패 결과(핵심 Assertion/에러 요약) 공유 후 사용자 확인 요청(큰 변경 아니면 자동 진행 가능, 불확실/옵션 선택 필요 시 확인 요구).
3. Green: 기능/구현 최소 수정으로 테스트 통과.
	- 구현 후 테스트 재실행 결과 전달.
4. Refactor: 중복/명명/추상화/패키지 구조 개선.
	- 리팩토링 전후 테스트 재실행으로 안전망 유지.
5. 품질 게이트: 빌드/테스트 통과 여부, 영향을 준 파일/주요 결론 요약.

### 테스트 작성 가이드
- Kotest `FunSpec` 패턴 유지. 새 시나리오는 `test("설명") { ... }` 형태.
- 도메인 순수 로직은 가능하면 애플리케이션 통합 테스트보다 단위/도메인 테스트에서 우선 검증.
- 파라미터/검증 실패 케이스: `shouldThrow<ConstraintViolationException> { ... }`.
- 중복/상태 전이 규칙 위반: 도메인 `check` 메시지 또는 커스텀 예외를 명시적으로 기대.

### 커뮤니케이션 규칙
- 동일한 계획/체크리스트를 매 응답마다 반복하지 말고 변화(delta)만 업데이트.
- 테스트 추가/수정 > 3개 파일 이상 변경 시 간단 체크포인트(무엇을 수정, 다음 단계) 보고.
- 외부 라이브러리 추가는 반드시 이유 + 최소 대안 비교.

### 예외: 단순 Q&A / 리드미 설명
- 코드 변경이 전혀 없는 순수 질의응답은 TDD 사이클 생략 가능 (명시).

위 규칙을 따르지 못하는 상황(예: 툴 제한, 불충분한 컨텍스트)이면 이유 + 대체 방식을 먼저 명시한다.

## 1. 전체 개요 (Big Picture)
- 스타일: 헥사고날(Ports & Adapters) + 도메인 모델 패턴.
- 흐름: 외부 액터 → 어댑터(`adapter/*`) → 애플리케이션 서비스(`application/*`) → 도메인(`domain/*`).
- 도메인 계층은 (필요한 JPA 어노테이션 외) Spring 프레임워크 의존을 두지 않고 순수하게 유지.
- 애플리케이션 계층은 유스케이스 오케스트레이션 담당.
- 어댑터 계층은 I/O 세부 구현 (현재는 비어있는 스켈레톤 다수).
- 회원 라이프사이클(등록, 활성화, 비활성화)과 중복 이메일 방지/검증 로직 구현됨.

## 2. 계층 / 패키지 컨벤션
- `domain/`: 엔티티 & 값 객체(`Member`, `Email`), 생성자/메서드에서 비즈니스 불변조건 수호 (`Email` 초기화, `Member.activate()` 등). 영속 목적 JPA 어노테이션 외 Spring 금지.
- `application/provided`: 외부에 노출하는 1차(Driving) 포트 (`MemberRegister`). 작고 의도 드러나는 이름.
- `application/required`: 인프라 의존 2차(Driven) 포트 (`MemberRepository`, `EmailSender`).
- `application/*.kt`: 구현 서비스 (`MemberService`) 가 제공 포트를 구현하고 필요한 포트/도메인만 의존. `@Service`, `@Transactional`, `@Validated` 사용.
- `adapter/*`: I/O 구현 스켈레톤. 구현 시 `<포트명>Impl` 또는 `<개념><Adapter>` 네이밍 (예: `JpaMemberRepository`, `SmtpEmailSender`). Spring 컴포넌트 스캔으로 빈 등록.

## 3. 검증(Validation) & 불변조건(Invariants)
- Bean Validation: `spring-boot-starter-validation` 로 활성화.
- Kotlin 데이터 클래스 프로퍼티에는 반드시 `@field:` use-site 필요 (`MemberRegisterRequest` 참고). 빠뜨리면 검증이 실행되지 않음.
- 메서드 파라미터 검증: 클래스 `@Validated`, 파라미터 `@Valid` 적용 (`MemberService.register`). 위반 시 `ConstraintViolationException` 기대.
- 도메인 불변조건은 `check(...)` + 의미 있는 한국어 메시지 사용.

## 4. 영속성 패턴
- Repository 포트는 Spring Data `Repository` 직접 확장 + 필요한 메서드만 명시 (불필요한 범용 CRUD 노출 금지).
- 시그니처에 원시 `String` 대신 값 객체(`Email`) 사용.
- 자연키: `Member.email` 에 `@NaturalId` 부여 + 애플리케이션 계층 중복 선확인 (`DuplicateEmailException`). 새 자연키 도입 시 동일 패턴 반복.

## 5. 테스트 컨벤션
- Kotest + JUnit5. Spring 통합 테스트는 `FunSpec` + `SpringExtension` + `@SpringBootTest`.
- 데이터 계층: `@DataJpaTest` 슬라이스 + 제약/assert 전에 `flush()` 강제 (`MemberRepositoryTest`).
- 테스트 더블: `support/TestContainersConfig` 에 Fake `PasswordEncoder`, `EmailSender` 정의; 새 Fake 추가 시 `@Primary` 로 우선순위 지정.
- 공용 픽스처: `MemberFixture` 재사용 (중복 생성 지양).

## 6. 비밀번호 처리
- 도메인은 `PasswordEncoder` 추상화만 인지. 구체 구현은 어댑터 또는 테스트 지원 계층에 둔다.

## 7. 신규 유스케이스 추가 예시
1. 순수 의미/검증 필요한 요청 DTO는 `domain` 에 정의 (필수: `@field:` 검증 어노테이션).
2. 제공 포트: `application/provided` 에 인터페이스 추가.
3. 서비스 구현: `application` 계층에서 포트 구현 + 필요한 포트 주입 + 도메인 팩토리 사용.
4. 새 외부 연동이 필요하면 `application/required` 에 포트 추가.
5. 어댑터 패키지에 구현 클래스 생성.
6. Kotest 통합 테스트 (`MemberRegisterTest` 패턴) 작성.

## 8. 오류 & 상태 전이 처리
- 비즈니스 규칙 위반은 도메인 예외(`DuplicateEmailException` 등) 사용.
- 상태 전이는 보호 메서드(`activate`, `deactivate`) 통해 검사. 새 Aggregate 도입 시 동일 패턴 확장.

## 9. Gradle & 빌드
- Kotlin 1.9 / Spring Boot 3.5 / Java 21 toolchain.
- 테스트 실행: `./gradlew test`.
- 의존성 추가: `build.gradle.kts` (가능한 Spring BOM 버전 관리 활용).
- 컴파일러 null 엄격 모드: `-Xjsr305=strict`.

## 10. 구현 시 유의사항
- 애플리케이션 서비스는 얇게: 오케스트레이션 + 도메인 팩토리(`Member.register`) 위임 + 포트 통한 부수효과.
- 의도적이지 않은 JPA 엔티티 외부 노출 지양; DTO 도입 시 매핑은 애플리케이션 계층에서.
- `application/provided` / `application/required` 포트 인터페이스에는 프레임워크 어노테이션 금지.
- 아웃바운드 어댑터 구현 클래스는 역할 드러나는 명명 (`JpaMemberRepository`, `SmtpEmailSender`).

## 11. 어댑터 스텁 확장 가이드
- 비어있는 하위 패키지는 향후 확장 의도 표식.
- 매핑: `persistence`=DB, `webapi`=REST, `security`=인증/인가, `integration`=외부 시스템 연동.

## 12. 흔한 실수(Pitfalls)
- 검증 어노테이션에 `@field:` 누락 → 테스트가 조용히 통과.
- 필요 없는 Spring Data 기본 메서드 노출 (`CrudRepository` 직접 확장) → 의도된 최소 표면 침식.
- 값 객체 대신 원시 `String` 사용 → 유비쿼터스 언어 약화.

항상 간결하게, 기존 패턴 준수, 가능한 한 “파일 1개 변화 + 대응 테스트” 형태로 증분 작업.


