package com.group.libraryapp.calculator

fun main() {
  val calculatorTest = CalculatorTest()
  calculatorTest.addTest()
  calculatorTest.minusTest()
  calculatorTest.multiplyTest()
  calculatorTest.divideTest()
  calculatorTest.divideExceptionTest()}

class CalculatorTest {

  fun addTest() {
    // given: 테스를 위한 준비
    val calculator = Calculator(5)

    // when: 테스트할 기능
    calculator.add(3)

    // then: 기대한 결과
    if (calculator.number != 8) {
      throw IllegalStateException();
    }
  }

  fun minusTest() {
    val calculator = Calculator(5)

    calculator.minus(3)

    if (calculator.number != 2) {
      throw IllegalStateException();
    }
  }

  fun multiplyTest() {
    val calculator = Calculator(5)

    calculator.multiply(3)

    if (calculator.number != 15) {
      throw IllegalStateException();
    }
  }

  fun divideTest() {
    val calculator = Calculator(5)

    calculator.divide(2)

    if (calculator.number != 2) {
      throw IllegalStateException();
    }
  }

  fun divideExceptionTest() {
    // given
    val calculator = Calculator(5)

    // when
    try {

      calculator.divide(0)
    } catch (e: IllegalArgumentException) {
      return
    } catch (e: Exception) {
      throw IllegalStateException();
    }
    throw IllegalStateException("기대하는 예외가 발생하지 않았습니다.")
  }
}