package com.group.libraryapp.domain.user.loanhistory

import org.springframework.data.jpa.repository.JpaRepository

interface UserLoanHistoryRepository: JpaRepository<UserLoanHistory, Long> {

  // UserLoanHistoryQuerydslRepository로 대체 (Querydsl 적용)
//  fun findByBookNameAndStatus(bookName: String, status: UserLoanStatus): UserLoanHistory?
//
//  fun findAllByStatus(status: UserLoanStatus): List<UserLoanHistory>
//
//  fun countByStatus(status: UserLoanStatus): Long
}
