package com.group.libraryapp.util

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull

fun fail(): Nothing {
  throw IllegalStateException()
}

// JPA에서 제공하는 findByIdOrNull 대신 findByIdOrThrow를 직접 커스텀으로 만들어 사용할 수 있다
fun <T, ID> CrudRepository<T, ID>.findByIdOrThrow(id: ID): T {
  return this.findByIdOrNull(id) ?: fail()
}