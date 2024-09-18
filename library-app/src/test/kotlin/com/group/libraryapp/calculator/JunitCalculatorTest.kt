package com.group.libraryapp.calculator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JunitCalculatorTest {

  @Test
  fun addTest() {
    // given
    val calculator = Calculator(5)

    // when
    calculator.add(3)

    // then
    assertThat(calculator.number).isEqualTo(8)
  }

  @Test
  fun minusTest() {
    val calculator = Calculator(5)

    calculator.minus(3)

    assertThat(calculator.number).isEqualTo(2)
  }

  @Test
  fun multiplyTest() {
    val calculator = Calculator(5)

    calculator.multiply(3)

    assertThat(calculator.number).isEqualTo(15)
  }

  @Test
  fun divideTest() {
    val calculator = Calculator(5)

    calculator.divide(2)

    assertThat(calculator.number).isEqualTo(2)
  }

  @Test
  fun divideExceptionTest() {
    // given
    val calculator = Calculator(5)

    // when & then
    val message = assertThrows<IllegalArgumentException> {
      calculator.divide(0)
    }.message
      assertThat(message).isEqualTo("0으로 나눌 수 없습니다")
  }

//  참고: apply 메서드를 사용할 수 있다 - 코드가 조금 더 간결해진다
//  @Test
//  fun divideExceptionTest() {
//    // given
//    val calculator = Calculator(5)
//
//    // when & then
//     assertThrows<IllegalArgumentException> {
//      calculator.divide(0)
//    }.apply {
//      assertThat(message).isEqualTo("0으로 나눌 수 없습니다")
//    }
//  }
}