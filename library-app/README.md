## 코틀린에서 테스트 진행

### isNull 검증
jetbrain의 nullable annotation을 사용하여 null 검증한다
cf. import org.jetbrains.annotations.Nullable

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