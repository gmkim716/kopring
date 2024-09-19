package com.group.libraryapp.dto.user.response

import com.group.libraryapp.domain.user.User

data class UserResponse(
  val id: Long,
  val name: String,
  val age: Int?
) {
  // 리팩토링 2: 정적 팩토리 메서드를 이용하는 방식으로 변경(companion object + of)
  // 생성자를 사용하지 않고 클래스 외부에서 객체를 생성할 수 있도록 함
  companion object {
    fun of(user: User): UserResponse {
      return UserResponse(user.id!!, user.name, user.age)
    }
  }
}

//{
//  // 리팩토링 1: 부 생성자를 이용하는 방식으로 변경
//  constructor(user: User) : this(user.id!!, user.name, user.age)
//}

//class UserResponse(user: User) {
//  val id: Long = user.id!!
//  val name: String = user.name
//  val age: Int? = user.age
//}
