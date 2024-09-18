## Java 서버를 Kotlin 서버로 리팩토링

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
  

## 코틀린에서 테스트 진행
### 작성한 모든 테스트 메서드 실행 방법
방법 1. (터미널) ./gradlew test
방법 2. gradle 탭 → Tasks → verification → test

### @DisplayName
테스트 메서드를 직관적으로 파악하기 위해 DisplayName을 사용한다
ex) @DisplayName("사용자 생성 테스트")

### 생성 테스트와 조회 테스트를 같이 돌렸을 때 실패하는 이유
- 두 테스트가 Spring Context를 공유한다
  1. 생성 테스트가 실행되면, Spring Context를 하나 띄우게 된다. 이때 내부적으로 H2 DB도 하나만 띄우게 된다.
  2. 생성 테스트로 인해 user가 1명 추가된다
  3. 조회 테스트가 과정에서 user가 2명 추가된다
  4. 조회 테스트의 hasSize(2) 검증이 실행되었을 때, 3명이 존재하기 때문에 검증에 실패한다 
  cf. 조회 테스트가 먼저 진행되더라도 발생하는 문제
→ 같은 DB를 공유하고 있기 때문에 발생하는 문제

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
- 참/거짓 검증 : .isTrue / .isFalse
  ex) assertThat(isNew).isTrue
- 개수 검증 : .hasSize
  ex) assertTh at(people  ).hasSize(3)
- 객체의 필드값을 추출해서 검증 : .extracting
  ex) assertThat(people).extracting("name").contains("martin", "dennis", "sophia")
- 순서 상관없이 값이 같은지 검증 : .containsExactlyInAnyOrder
  ex) assertThat(people).extracting("name").containsExactlyInAnyOrder("martin", "dennis", "sophia")
- 순서까지 값이 같은지 검증 : .containsExactly
  ex) assertThat(people).extracting("name").containsExactly("martin", "dennis", "sophia")
- function1 함수를 실행했을 때 IllegalArgumentException이 나오는지 검증 : .assertThrows
  ex) assertThrows<IllegalArgumentException> { function1() }
- 예외 메시지 검증 : .message
  ex) val message = assertThrows<IllegalArgumentException> { function1() }.message
      assertThat(message).isEqualTo("잘못된 값이 들어왔습니다")

### JUnit5
- @Test : 테스트 메서드 지정
- @BeforeEach : 각 테스트 메서드가 실행되기 전에 실행
- @AfterEach : 각 테스트 메서드가 실행된 후에 실행
- @BeforeAll : 모든 테스트 메서드가 실행되기 전에 1번만 실행
- @AfterAll : 모든 테스트 메서드가 실행된 후에 1번만 실행

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
1) data Class 사용
  ex) data class Calculator ( private var number: Int ) { ... }
2) 변수를 public으로 사용
   ex) class Calcualtor ( var number: Int ) { ... } 
3) 코틀린의 backing properties 사용
  ex) class Calculator ( private var _number: Int ) { var number: int get() = this._number } ...

cf. 강사님의 주관에 따라 2번 방식을 사용


### data 클래스
- toString(), hashCode(), equals(), copy() 메서드를 자동으로 만들어주는 클래스 


## 자바 프로젝트에서 코틀린 프로젝트로 전환하기
### build.gradle
- plugin : id 'org.jetbrains.kotlin.jvm' version '1.6.21'
- dependencies : implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8" 
- compileKotlin : kotlinOptions { jvmTarget = "11" }
- compileTestKotlin : kotlinOptions { jvmTarget = "11" }