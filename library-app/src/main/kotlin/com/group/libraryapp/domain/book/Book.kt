package com.group.libraryapp.domain.book

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Book (
  val name: String,

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null
) {
  init {
    if (name.isBlank()) {
      throw IllegalArgumentException("책 이름은 빈 문자열일 수 없습니다")
    }
  }
}