# Summary

### What's the Project for

- Convert Java project to Kotlin
- How to Test Driven Development
- Make MVC server
- How to control DB(JPA, JPQL, Querydsl) in Kotlin

### build.gradle

```gradle
plugin {
  id 'org.jetbrains.kotlin.jvm' version '1.6.21'
  id "org.jetbrains.kotlin.plugin.spring" version "1.6.21"
  id 'org.jetbrains.kotlin.plugin.jpa' version '1.6.21'
  id 'org.jetbrains.kotlin.plugin.reflect' version '1.6.21'
  id "org.jetbrains.kotlin.plugin.allopen" version "1.6.21"
}

dependencies {
  implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
  implementation "com.fasterxml.jackson.module:jackson-module-kotlin"
}

compileKotlin {
  kotlinOptions {
    jvmTarget = "11"
  }
}

compileTestKotlin {
  kotlinOptions {
    jvmTarget = "11"
  }
}
```

# Notes

## 섹션 0. 자바 프로젝트에서 코틀린 프로젝트로 전환하기

### build.gradle

- plugin : id 'org.jetbrains.kotlin.jvm' version '1.6.21'
- dependencies : implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
- compileKotlin : kotlinOptions { jvmTarget = "11" }
- compileTestKotlin : kotlinOptions { jvmTarget = "11" }

<br/>

## 섹션 1. 코틀린에서 테스트 진행

### 작성한 모든 테스트 메서드 실행 방법

방법 1. (터미널) ./gradlew test
방법 2. gradle 탭 → Tasks → verification → test

### @DisplayName

테스트 메서드를 직관적으로 파악하기 위해 DisplayName을 사용한다
ex) @DisplayName("사용자 생성 테스트")

### 생성 테스트와 조회 테스트를 같이 돌렸을 때 실패하는 이유

- 두 테스트가 Spring Context를 공유한다

  1. 생성 테스트가 실행되면, Spring Context를 하나 띄우게 된다. 이때 내부적으로 H2 DB도 하나만 띄우게 된다

  2. 생성 테스트로 인해 user가 1명 추가된다

  3. 조회 테스트가 과정에서 user가 2명 추가된다

  4. 조회 테스트의 hasSize(2) 검증이 실행되었을 때, 3명이 존재하기 때문에 검증에 실패한다

  cf. 조회 테스트가 먼저 진행되더라도 발생하는 문제 → 같은 DB를 공유하고 있기 때문에 발생하는 문제

따라서, 공유 자원으로 사용되는 DB를 깨끗하게 관리해야 한다

- userRepository.deleteAll()을 사용할 수도 있지만, 이때 테스트 코드간의 중복이 발생한다
- @AfterEach를 사용하여 테스트가 끝날 때마다 실행되는 코드를 작성한다

### isNull 검증

jetbrain의 nullable annotation을 사용하여 null 검증한다
cf. import org.jetbrains.annotations.Nullable

### SpringBootTest

- SpringContext가 관리하는 Bean을 테스트할 때 사용해야 하는 어노테이션
- SpringContext가 관리 : Service, Repository, Component, Controller

### 자주 사용되는 단언문

- 참/거짓 검증 : `.isTrue / .isFalse`

  ex) assertThat(isNew).isTrue

- 개수 검증 : `.hasSize`

  ex) assertTh at(people ).hasSize(3)

- 객체의 필드값을 추출해서 검증 : `.extracting`

  ex) assertThat(people).extracting("name").contains("martin", "dennis", "sophia")

- 순서 상관없이 값이 같은지 검증 : `.containsExactlyInAnyOrder`

  ex) assertThat(people).extracting("name").containsExactlyInAnyOrder("martin", "dennis", "sophia")

- 순서까지 값이 같은지 검증 : `.containsExactly`

  ex) assertThat(people).extracting("name").containsExactly("martin", "dennis", "sophia")

- function1 함수를 실행했을 때 IllegalArgumentException이 나오는지 검증 : `.assertThrows`

  ex) assertThrows<IllegalArgumentException> { function1() }

- 예외 메시지 검증 : `.message`

  ex) val message = assertThrows<IllegalArgumentException> { function1() }.message
  assertThat(message).isEqualTo("잘못된 값이 들어왔습니다")

### JUnit5

- `@Test` : 테스트 메서드 지정

- `@BeforeEach` : 각 테스트 메서드가 실행되기 전에 실행

- `@AfterEach` : 각 테스트 메서드가 실행된 후에 실행

- `@BeforeAll` : 모든 테스트 메서드가 실행되기 전에 1번만 실행

- `@AfterAll` : 모든 테스트 메서드가 실행된 후에 1번만 실행

cf. BeforeAll, AfterAll은 static 메서드로 선언해야 한다

```kotlin
  companion object {
    @BeforeAll
    @JvmStatic
    fun beforeAll() {
        println("모든 테스트 시작 전")
    }
}
```

### 방식 2가지

1. data Class 사용

   ex) data class Calculator ( private var number: Int ) { ... }

2. 변수를 public으로 사용

   ex) class Calcualtor ( var number: Int ) { ... }

3. 코틀린의 backing properties 사용

   ex) class Calculator ( private var \_number: Int ) { var number: int get() = this.\_number } ...

cf. 강사님의 주관에 따라 2번 방식을 사용

### data 클래스

- toString(), hashCode(), equals(), copy() 메서드를 자동으로 만들어주는 클래스

<br/>

## 섹션 2. Java 서버를 Kotlin 서버로 리팩토링

### JSON parse error

- Jackson이 Kotlin을 지원하지 않아 발생하는 문제

- build.gradle: dependencies { implementation "com.fasterxml.jackson.module:jackson-module-kotlin" } 추가

### Controller를 kotlin으로 변경

### Dto를 kotlin으로 변경

- 간단하지만 양이 많은 것이 특징
- Convert Java to Kotlin 실행, 단축키(command + option + shift + k) 활용
- null 가능 여부와 같이, 잘못된 변환이 있을 수 있으므로 변환된 코드를 확인해 볼 필요가 있다
- data class로 변경하면 디버깅 과정에서 equals, hashCode, toString 메서드를 사용할 수 있다
- 정적 팩토리 메서드 적용

### Service를 kotlin으로 변경

- kotlin에서는 상속 및 오버라이드를 사용하지 않는다. 만약 필요한 경우 open 키워드를 사용한다.
- open을 매번 사용하는 것이 번거로울 때 plugin을 추가할 수 있다 → build.gradle: plugins { id 'org.jetbrains.kotlin.plugin.spring' version '1.6.21' } 추가

### Repository를 kotlin으로 변경

- @Repository 어노테이션을 필요로 하지 않는다
- Optional 대신 ?를 사용한다
- 반복되는 IllegalArgumentException → ExceptionUtils와 같이 반환 타입이 Nothing인 메서드를 만들어 사용할 수 있다
- CrudRepository를 통해 메서들을 직접 만들어 JPA를 사용할 수 있다. cf. ExceptionUtils 참고하기

### 도메인 계층을 kotlin으로 변경

### plugin 추가

- JPA 사용: Entity를 등록할 때 기본 생성자를 필요로 하게 된다
  → build.gradle: plugins { id 'org.jetbrains.kotlin.plugin.jpa' version '1.6.21' } 추가
- kotlin/reflect/full/KClasses
  → build.gradle: dependencies { id 'org.jetbrains.kotlin.plugin.reflect' version '1.6.21' } 추가

### 리팩토링 계획

1. Domain
   - 특징 : POJO, JPA Entity 객체
2. Repository
   - 특징 : Spring Bean, 의존성 X
3. Service
   - 특징 : Spring Bean, 의존성 O, 비즈니스 로직
4. Controller

- 특징 : Spring Bean, 의존성 O, DTO의 경우 숫자가 많다

<br/>

## 섹션 3. Kotlin과 JPA를 함께 사용할 때 주의할 점

### 1. Setter

1. var 타입의 프로퍼티
2. setter 대신 update 함수를 만들어 사용

- setter 대신 좋은 이름의 함수를 만들어 사용하는 것이 훨씬 clean 하다
- 하지만, setter는 public이기 때문에, 유저 이름 업데이트 기능에서 setter를 사용할'수도' 있다
- 코드 상, setter를 사용할'수도' 있다는 점이 불편하다

→ setter를 private 하게 만드는 방법 2가지

1. backing property 사용

```kotlin
class User (
  private var _name: String
) {
  val name: String
    get() = this._name
}
```

2. custom setter 사용

```kotlin
class User(
  name: S
) {
  var name = name
    private set
}
```

그러나, 두 방법 모두 프로퍼티가 많아지면 번거롭다
따라서, 강사 기준으로 setter를 열어두지만 사용하지 않는 방법을 선호한다
trade-off의 영역이므로, 팀 컨벤션에 맞게 활용하도록 한다

### 2. 생성자 안의 property vs 클래스 body 안의 property

property를 생성자 안에만 선언해야 하는가? → No, 클래스 body 안에도 선언할 수 있다

강사님의 의견에 따르면, 기준이 있어야 한다

1. 모든 property를 생성자에 넣거나
2. property를 생성자, body 안에 넣을 때 구분되는 명확한 기준이 있거나

### JPA와 data class

- 결론: Entity는 data class로 만들지 않는다
- 이유: equals, hashCode, toString은 모두 JPA Entity에 100% 적합하진 않는 메서드이기 때문이다
  ex) User 클래스의 equals를 호출함으로 인해, userHistory 클래스의 equals가 호출된다. 이때 다시 User 클래스의 equals가 호출되면서 무한 호출되는 문제가 발생할 수 있다
- Tip: Entity가 생성되는 로직을 찾고싶다면, constructor 를 찾아보자
  ex) 클래스에 constructor를 추가해서 클릭해보면 실제 생성되는 로직을 확인할 수 있다

## 섹션 4. 첫 번째 요구사항 추가

### Boolean을 Enum으로 변경하기

- boolean으로 관리하는 타입이 여러 개이면, 이해하기 어려워진다
- boolean으로 표현되는 타입의 조합 상태가 모두 유의미하지는 않다
  해결: Enum을 도입하면 코드의 가독성을 높일 수 있다

### Enum이 숫자로 DB에 저장되면 발생하는 문제

1. 기존 Enum의 순서가 바뀌면 아주 큰일이 난다
2. 기존 Enum 타입의 삭제, 새로운 Enum 타입의 추가가 제한적이다
   해결: @Enumerated(EnumType.STRING)을 사용하여 Enum 타입을 String으로 변환하여 저장한다

### Enum 타입 사용 이유

- 사용 이유 (if문을 통해 진행하는 type: String의 단점)
  1. 현재 검증되지 않고 있으며, 검즈 코드를 추가 작성하기 번거롭다
  2. 코드만 보았을 때 어떤 값이 DB에 있는지 알 수 없다
  3. type과 관련한 새로운 로직을 작성할 때 번거롭다

### ObjectMother 패턴, Test Fixture 패턴을 사용한 객체 생성

- book 객체를 만드는 함수를 미리 만들어둔다: 생성자를 통해 만드는 것이 아닌, 정적 팩토리 메서드를 이용
- ex) companion object{ fun fixture () : Book { return Book(...) } }
  → 새로운 필드가 추가되더라도 테스트 코드를 수정하지 않아도 된다 :)

### 구현목표: 책의 분야 추가하기

- type, status를 서버에서 관리하는 방법과 장단점에 대해 이해한다
- test fixture의 필요성을 느끼고 구성하는 방법을 알아본다
- kotlin에서 enum + JPA + Spring Boot를 활용하는 방법을 알아본다

<br />

## 섹션 5. 두 번째 요구사항 추가

### 정적 팩토리 메서드를 이용해 코드를 깔끔하게 작성할 수 있다

### N+1 문제의 해결, Fetch Join

ex) @Query("select u from User u join fetch u.userLoanHistories")

- fetch join을 사용하면, User를 가져올 때, UserLoanHistory도 함께 가져온다
- 기존의 지연로딩과 달리 즉시로딩(Eager Loading)이 사용된다
- N+1 문제를 방지할 수 있다

### SQL Join 쿼리에 대한 이해

- Join 쿼리: 쿼리 한 번으로, 여러 테이블의 결과를 한 번에 보는 방법
- 별칭을 사용해 테이블을 구분할 수 있다
- 같은 데이터 구조에 대해 하나만 적용하려면 DISTINCT를 사용한다

### N+1 문제

- 최초에 모든 유저를 가져오고 (쿼리 1회), Loop를 통해 유저별로 히스토리를 가져오는 경우 (쿼리 n회)
- JPA 1:N 연관관계의 동작 원리
  1. 최초 유저 로딩 시 가짜 List<UserLoanHistory>가 들어간다: 시작부터 진짜 모든 데이터를 가져오는 것은 비효율적일 수 있기 때문
  2. 실제 히스토리에 접근할 때 진짜 UserLoanHistory를 불러온다
- 이를, Lazy Fetching 전략이라고 부르는데, N+1 문제가 발생하면 성능을 저하시키는 원인이 된다

### 테스트 코드 작성

- 1개의 복잡한 테스트 코드보다 2개의 간단한 테스트 코드를 작성하는 것이 좋다
  1. 테스트 코드가 복잡해지면, 테스트 코드를 이해하는데 시간이 오래 걸린다
  2. 여러 단언문이 하나의 테스트 코드에 선언되어 있을 때, 앞선 단언문의 결과가 실패하면, 뒤의 단언문은 실행되지 않는다

### Controller를 구분하는 기준

1. 화면에서 사용되는 API끼리 모아둔다
2. 동일한 도메인끼리 모아둔다
3. (간혹) 1 API 1 Controller를 사용한다
   중요한건, 프로젝트가 낯선 사람 입장에서 Controller가 어디 있는지 찾을 수 있도록 하는 것

4. intelliJ 전체 검색으로 api를 탐색
5. URL들을 모아두기
6. (유료계정을 사용하는 경우) intelliJ endpoints 사용
   강사님의 경우 2번 방식 채택

### 구현목표: 도서 대출 현황

- Join 쿼리의 종류와 차이점을 이해한다
- JPA n+1 문제가 무엇이고 발생하는 원인을 이해한다
- n+1 문제를 해결하기 위한 방법을 이해하고 활용한다
- 새로운 API를 만들 때 생길 수 잇는 고민 포인트를 이해하고, 적절한 감을 잡는다

<br />

## 섹션 6. 세 번째 요구사항 추가

### 구현목표: 책 통계

- SQL의 다양한 기능들을 활용한다
- 간결한 함수형 프로그래밍 기법을 사용하고 익숙해진다
- 동일한 기능을 App과 DB로 구현해보고 차이점을 이해한다

<br />

## 섹션 7. 네 번째 요구사항 추가 - Querydsl

### 정리

1. JPQL과 Querydsl의 장단점을 이해한다
2. Querydsl을 Kotlin + Spring Boot와 함께 사용하고 2가지 방식의 장단점을 이해한다
3. Querydsl의 기본적인 사용법을 익힌다
4. Querydsl의 기존 Repository를 리팩토링 한다

### UserLoanHistoryRepository를 Querydsl으로 리팩토링 하기

- @Query를 사용하지 않은 Repository 기능들도 Querydsl로 변경해야 할까?
  → 강사님의 의견으로는 추천한다. 동적 쿼리의 간편함 때문
- 필드와 조건이 늘어날수록 함수의 명칭이 늘어날 수 있다
  ex) findByA, findByAAndB, findByAAndC, findByAAndD ..
- UserLoanHistoryRepository 코드 참고하기

### 첫 번째 방식 vs 두 번째 방식, 어떤 방식이 더 좋을까

- 2번째 방식을 추천한다. 멀티 모듈을 사용하는 경우 모듈 별로만 Repository를 쓰는 경우가 많기 때문이다
- 모듈1 Repository, 모듈2 Repository - Core 모듈을 연결해 각각 사용하는 방식을 사용하다 보니 2번 방식을 추천한다 (...?)
- Core 모듈에서는 Spring Data JPA Repository를 기본으로, 다른 모듈에서 사용할 수 있도록 한다

### Querydsl 사용하기, 두 번째 방법

- Projections.constructor : Query문을 통해 모든 속성을 가져오는 것이 아닌, 특정 요소만 가져오기 = 주어진 Dto의 생성자를 호출한다
- 뒤에 나오는 파라미터들이 생성자로 들어간다

장점: 클래스만 바로 만들면 되어 간결하다
단점: 필요에 따라 여러 Repository를 모두 불러와야 한다

### Querydsl 사용하기, 첫 번째 방법

1. UserRepositoryCustom 생성
2. UserRepository가 상속받을 수 있도록 설
3. UserRepositoryCustomImpl 생성
4. QuerydslConfig 생성: querydsl 작성에 필요한 Bean을 등록한다

장점: 서비스단에서 UserRepository 하나만 사용하면 된다  
단점: 인터페이스와 클래스를 항상 같이 만들어주어야 하는 것이 부담이고 번거롭다

### Querydsl 도입하기

- JPQL의 단점
  1. 문자열로 쿼리문을 작성하기 때문에 버그를 찾기 어렵다
  2. 문법이 조금 달라 그때마다 검색해 찾아봐야 한다
  3. 동적 쿼리 작성이 어렵다
  4. 도메인 코드 변경에 취약하다
- JPQL의 단점을 보완하기 위해 Querydsl을 도입한다

- build.gradle: plugins { id 'org.jetbrains.kotlin.kapt' version '1.6.21' }
  build.gradle: dependencies { implementation 'com.querydsl:querydsl-jpa:5.0.0' }
  build.gradle: dependencies { kapt("com.querydsl:querydsl-apt:5.0.0:jpa") }
  build.gradle: dependencies { kapt("org.springframework.boot:spring-boot-configuration-processor") }

- build > generated > source > kapt > main > ... > Q 클래스 생성된 모습 확인 가능

### 구현 목표

- JPQL과 Querydsl의 장단점을 이해할 수 있다
- Querydsl을 Kotlin + Spring Boot와 함께 사용할 수 있다
- Querydsl을 활용해 기존에 존재하던 Repository를 리팩토링 할 수 있다

<br />

## 섹션 8. 마지막 세션

### 강의에서 다룬 내용

- 언어: Kotlin
  빌드 툴: Gradle
  서버 프레임워크: Spring Boot
  DB 접근 기술: JPA (JPQL, fetch join, Querydsl)
  DB: SQL (join, sum, avg, count, group by, order by)

- 테스트 코트: 테스트 코드란 무엇인지 / JUnit5 / Test Fixture
  클린 코드: 정적 팩토리 메소드 / 함수형 프로그래밍
  설계 역량: Enum class / 애플리케이션과 DB 사이의 trade-off

- PDF 부록: Jackson

<br />

## 추가 강의

문제 상황: 테스트 코드에서 연관관계를 갖는 경우, 영속성 컨텍스트에 저장된 객체를 가져올 때, 객체의 필드를 가져오지 않는 경우가 있다
제목: 테스트 코드와 LazyInitializationException

방법 1: @Transactional을 사용하여 해결

```kotlin
@Transactional
@Test
fun test() { ... }
```

- 장점: 간결하다, 롤백이 된다, 트랜잭션별로 테스트를 격리할 수 있어 병렬 테스트가 가능하다

- 단점: 테스트 내성이 떨어진다

방법 2: N쪽의 Repository를 활용한다 = 원래는 lazyFetching이 진행되어야 하는 부분을 강제로 가져온다

```kotlin
@SpringBootTest
class TempTest @Autowired constructor(
  private val userService: UserService,
  private val userRepository: UserRepository,
  private val userLoanHistoryRepository: UserLoanHistoryRepository  // 1:n에서 n측면의 Repository
) {
  ...
}
```

방법 3: fetch join을 사용해 미리 불러온다 = fetch join을 사용하면, 연관관계를 가진 객체를 한 번에 가져올 수 있다

- 단점: 1:N 관계에서 하나의 repository에 대해서만 사용 가능

방법 4: TxHelper를 이용한다

```kotlin
@Component
class TxHelper {
  @Transactional
  fun exec(block: () -> Unit) {
    block()
  }
}
```

<br />

## 추가 강의 - 코프링과 플러그인

1. 스프링 컴포넌트와 spring 플러그인: id "org.jetbrains.kotlin.plugin.spring" version "1.6.21"

2. JPA 객체와 기본 생성자: id "org.jetbrains.kotlin.plugin.jpa" version "1.6.21"

3. JPA 객체와 open: id "org.jetbrains.kotlin.plugin.allopen" version "1.6.21"
   allOpen {
   annotation("javax.persistence.Entity")
   annotation("javax.persistence.MappedSuperclass")
   annotation("javax.persistence.Embeddable")
   }
