package com.group.libraryapp.repository.user

import com.group.libraryapp.domain.user.loanhistory.QUserLoanHistory.userLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.querydsl.jpa.JPQLQueryFactory
import org.springframework.stereotype.Component

@Component
class UserLoanHistoryQuerydslRepository (
  private val queryFactory: JPQLQueryFactory,
) {

  // status를 추가적으로 받을 수 있도록 함
  fun find(bookName: String, status: UserLoanStatus? = null): UserLoanHistory? {
    return queryFactory.select(userLoanHistory)
      .from(userLoanHistory)
      .where(
        userLoanHistory.bookName.eq(bookName),
        status?.let { userLoanHistory.status.eq(status) }  // let: null 안정성을 보장하면서 status가 있을 때만 특정 로직을 수행
      )
      .limit(1)
      .fetchOne()
  }

  fun count(status: UserLoanStatus): Long {
    return queryFactory.select(userLoanHistory.id.count())
      .from(userLoanHistory)
      .where(
        userLoanHistory.status.eq(status)
      )
      .fetchOne() ?: 0L
  }
}