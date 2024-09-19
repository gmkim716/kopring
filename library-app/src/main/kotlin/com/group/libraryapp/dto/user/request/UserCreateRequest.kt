package com.group.libraryapp.dto.user.request

data class UserCreateRequest(
  val name: String,
  val age: Int?
)  // Java의 Integer는 null을 허용하지만, Kotlin의 Int는 null을 허용하지 않는다
