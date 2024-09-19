package com.group.libraryapp.domain.book

import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository: JpaRepository<Book, Long> {

  fun findByName(bookName: String): Book?

//  // Querydsl로 대체된 코드
//  @Query("SELECT NEW com.group.libraryapp.dto.book.response.BookStatResponse(b.type, COUNT(b.id))" +
//          " FROM Book b GROUP BY b.type")
//  fun getStats(): List<BookStatResponse>
}