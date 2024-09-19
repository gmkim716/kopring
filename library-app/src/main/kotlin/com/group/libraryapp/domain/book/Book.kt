package com.group.libraryapp.domain.book

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Book (
  val name: String,
  val type: String,

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null
) {
  init {
    if (name.isBlank()) {
      throw IllegalArgumentException("책 이름은 빈 문자열일 수 없습니다")
    }
  }

  // 정적 팩토리 메서드를 이용해 객체 생성
  companion object {
    fun fixture(
      name: String = "책 이름",
      type: String = "COMPUTER",
      id: Long? = null,
    ) : Book {
      return Book(
        name = name,
        type = type,
        id = id,
      )
    }
  }
}