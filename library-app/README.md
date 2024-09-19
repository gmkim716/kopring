## 섹션 4.새로운 요구사항 추가


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

강사의 의견에 따르면, 기준이 있어야 한다
1. 모든 property를 생성자에 넣거나
2. property를 생성자, body 안에 넣을 때 구분되는 명확한 기준이 있거나

### JPA와 data class
- 결론: Entity는 data class로 만들지 않는다 
- 이유: equals, hashCode, toString은 모두 JPA Entity에 100% 적합하진 않는 메서드이기 때문이다
  ex) User 클래스의 equals를 호출함으로 인해, userHistory 클래스의 equals가 호출된다. 이때 다시 User 클래스의 equals가 호출되면서 무한 호출되는 문제가 발생할 수 있다
- Tip: Entity가 생성되는 로직을 찾고싶다면, constructor 를 찾아보자
ex) 클래스에 constructor를 추가해서 클릭해보면 실제 생성되는 로직을 확인할 수 있다 

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
  

## 섹션 1. 코틀린에서 테스트 진행
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

## 섹션 0. 자바 프로젝트에서 코틀린 프로젝트로 전환하기
### build.gradle
- plugin : id 'org.jetbrains.kotlin.jvm' version '1.6.21'
- dependencies : implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8" 
- compileKotlin : kotlinOptions { jvmTarget = "11" }
- compileTestKotlin : kotlinOptions { jvmTarget = "11" }