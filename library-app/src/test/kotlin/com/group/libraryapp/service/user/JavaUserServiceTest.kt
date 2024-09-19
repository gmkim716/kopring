package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JavaUserServiceTest @Autowired constructor (
  private val userService: UserService,
  private val userRepository: UserRepository,
  private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

  @AfterEach
  fun clean() {
    println("CLEAN 시작")
    userRepository.deleteAll()
  }

  @Test
  @DisplayName("유저 저장이 정상 동작한다")
  fun saveUserTest() {
    // given
    val request = UserCreateRequest("김경민", null)

    // when
    userService.saveUser(request)

    // then
    val results = userRepository.findAll()
    assertThat(results).hasSize(1)
    assertThat(results[0].name).isEqualTo("김경민")
    assertThat(results[0].age).isNull()
  }

  @Test
  @DisplayName("유저 조회가 정상 동작한다")
  fun getUserTest() {
    // given
    userRepository.saveAll(listOf(
      User("A", 20),
      User("B", null),
    ))

    // when
    val results = userService.getUsers()

    // then
    assertThat(results).hasSize(2)
    assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B")
    assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
  }

  @Test
  @DisplayName("유저 업데이트가 정상 동작한다")
  fun updateUserTest() {
    // given
    val savedUser = userRepository.save(User("A", null))
    val request = UserUpdateRequest(savedUser.id!!, "B")  // id는 null이 절대 아니다는 것을 전달하기 위해 !! 사용

    // when
    userService.updateUserName(request)

    // then
    val results = userRepository.findAll()[0]
    assertThat(results.name).isEqualTo("B")
  }

  @Test
  @DisplayName("유저 삭제가 정상 동작한다")
  fun deleteUserTest() {
    // given
    userRepository.save(User("A", null))

    // when
    userService.deleteUser("A")

    // then
    assertThat(userRepository.findAll()).isEmpty()  // hasSize(0)과 같은 의미
  }

  @Test
  @DisplayName("대출 기록이 없는 유저도 응답에 포함된다")
  fun getUserLoanHistoriesTest1() {
    // given
    userRepository.save(User("A", null))

    // when
    val results = userService.getUserLoanHistories()

    // then
    assertThat(results).hasSize(1)
    assertThat(results[0].name).isEqualTo("A")
    assertThat(results[0].books).isEmpty()
  }

  @Test
  @DisplayName("대출 기록이 많은 유저의 응답이 정상 동작한다")
  fun getUserLoanHistoriesTest2() {
    // given
    val savedUser = userRepository.save(User("A", null))
    userLoanHistoryRepository.saveAll(listOf(
      UserLoanHistory.fixture(savedUser, "책1", UserLoanStatus.LOANED),
      UserLoanHistory.fixture(savedUser, "책2", UserLoanStatus.LOANED),
      UserLoanHistory.fixture(savedUser, "책3", UserLoanStatus.RETURNED)
    ))

    // when
    val results = userService.getUserLoanHistories()

    // then
    assertThat(results).hasSize(1)
    assertThat(results[0].name).isEqualTo("A")
    assertThat(results[0].books).hasSize(3)
    assertThat(results[0].books).extracting("name").containsExactlyInAnyOrder("책1", "책2", "책3")
    assertThat(results[0].books).extracting("isReturn").containsExactlyInAnyOrder(false, false, true)
  }

  // 비 추천: 복잡한 테스트 1개보다 간단한 테스트 2개가 용이하다, 여러 단언문을 시도하는 경우 앞선 단언문 실패로 인해 이후 단언문이 실행되지 않을 수 있다
  @Test
  @DisplayName("getUserLoanHistoriesTest1, 2가 합쳐진 테스트")
  fun getUserLoanHistoriesTest3() {
    // given
    val savedUsers = userRepository.saveAll(listOf(
      User("A", null),
      User("B", null),
    ))

    userLoanHistoryRepository.saveAll(listOf(
      UserLoanHistory.fixture(savedUsers[0], "책1", UserLoanStatus.LOANED),
      UserLoanHistory.fixture(savedUsers[0], "책2", UserLoanStatus.LOANED),
      UserLoanHistory.fixture(savedUsers[0], "책3", UserLoanStatus.RETURNED)
    ))

    // when
    val results = userService.getUserLoanHistories()

    // then
    assertThat(results).hasSize(2)
    val userResult = results.find { it.name == "A" }!!

    assertThat(userResult.books).hasSize(3)
    assertThat(userResult.books).extracting("name").containsExactlyInAnyOrder("책1", "책2", "책3")
    assertThat(userResult.books).extracting("isReturn").containsExactlyInAnyOrder(false, false, true)

    val userResult2 = results.find { it.name == "B" }!!
    assertThat(userResult2.books).isEmpty()
  }
}