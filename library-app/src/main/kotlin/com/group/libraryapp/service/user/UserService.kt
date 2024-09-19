package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.dto.user.response.BookHistoryResponse
import com.group.libraryapp.dto.user.response.UserLoanHistoryResponse
import com.group.libraryapp.dto.user.response.UserResponse
import com.group.libraryapp.util.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
  private val userRepository: UserRepository,
) {

  @Transactional
  fun saveUser(request: UserCreateRequest) {
    val newUser = User(request.name, request.age)
    userRepository.save(newUser)
  }

  @Transactional(readOnly = true)
  fun getUsers(): List<UserResponse> {
//    return userRepository.findAll().map { UserResponse(it) }  // kotlin에서 parameter가 하나일 때 it으로 대체 가능
//    return userRepository.findAll().map(::UserResponse)
    return userRepository.findAll()
      .map { user -> UserResponse.of(user) }
  }

  @Transactional
  fun updateUserName(request: UserUpdateRequest) {
    val user = userRepository.findByIdOrThrow(request.id)
    user.updateName(request.name)
  }

  @Transactional
  fun deleteUser(name: String) {
    val user = userRepository.findByName(name) ?: throw IllegalArgumentException()
    userRepository.delete(user)
  }

  @Transactional
  fun getUserLoanHistories(): List<UserLoanHistoryResponse> {
//   // 리팩토링 전: 객체 생성 로직이 getUserLoanHistories 메서드 내부에 하드코딩 되어 있어 유지보수 및 재사용이 어려움
//    return userRepository.findAllWithHistories().map { user ->
//      UserLoanHistoryResponse(
//        name = user.name,
//        books = user.userLoanHistories.map { history ->
//          BookHistoryResponse(
//            name = history.bookName,
//            isReturn = history.status == UserLoanStatus.RETURNED
//          )
//        }
//      )

//    // 리팩토링 1: companion object의 of 메서드 사용
//    // 중복된 객체 생성 로직 제거, BookHistoryResponse의 생성 로직을 별도의 정적 팩토리 메서드로 분리, 코드가 간결해지고 재사용할 수 있게 되었다
//    return userRepository.findAllWithHistories().map { user ->
//      UserLoanHistoryResponse(
//        name = user.name,
//        books = user.userLoanHistories.map(BookHistoryResponse::of)
//      )
//    }

//    // 리팩토링 2: UserLoanHistoryResponse 생성 로직도 팩토리 메서드를 통해 캡슐화
//    return userRepository.findAllWithHistories().map { user ->
//      UserLoanHistoryResponse.of(user)
//    }

    return userRepository.findAllWithHistories()
      .map(UserLoanHistoryResponse::of)
  }
}