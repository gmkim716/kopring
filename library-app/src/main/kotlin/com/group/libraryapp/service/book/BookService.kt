package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import com.group.libraryapp.repository.book.BookQuerydslRepository
import com.group.libraryapp.util.fail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService (

  private val bookRepository: BookRepository,
  private val bookQuerydslRepository: BookQuerydslRepository,
  private val userRepository: UserRepository,
  private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

  @Transactional
  fun saveBook(request: BookRequest) {
    val book = Book(request.name, request.type)
    bookRepository.save(book)
  }

  @Transactional
  fun loanBook(request: BookLoanRequest) {
    val book = bookRepository.findByName(request.bookName) ?: fail()
    if (userLoanHistoryRepository.findByBookNameAndStatus(request.bookName, UserLoanStatus.LOANED) != null) {
      throw IllegalArgumentException("진작 대출되어 있는 책입니다")
    }

    val user = userRepository.findByName(request.userName) ?: fail()
    user.loanBook(book)
  }

  @Transactional
  fun returnBook(request: BookReturnRequest) {
    val user = userRepository.findByName(request.userName) ?: fail()
    user.returnBook(request.bookName)
  }

  @Transactional(readOnly = true)
  fun countLoanedBook(): Int {
    return userLoanHistoryRepository.countByStatus(UserLoanStatus.LOANED).toInt()
  }

  @Transactional(readOnly = true)
  fun getBookStatistics(): List<BookStatResponse> {
//    val results = mutableListOf<BookStatResponse>()
//
//    val books = bookRepository.findAll()
////    for (book in books) {
////      val targetDto = results.firstOrNull() { dto -> book.type == dto.type }
////      if (targetDto == null) {
////        results.add(BookStatResponse(book.type, 1))
////      } else {
////        targetDto.plusOne()
////      }
////    }
//
//    // 리팩토링 1: 간결하게 수정
//    for (book in books) {
//      results.firstOrNull() { dto -> book.type == dto.type }?.plusOne()
//        ?: results.add(BookStatResponse(book.type, 1))
//    }
//    return results
//  }

//    // 리팩토링 2: groupBy 사용
//    return bookRepository.findAll() // List<Book>
//      .groupBy { book -> book.type } // Map<BookType, List<Book>>
//      .map { (type, books) -> BookStatResponse(type, books.size) } // List<BookStatResponse>
//  }

    // 리팩토링 3: groupBy 사용 (DB에서 처리)
    return bookQuerydslRepository.getStats()
  }
}