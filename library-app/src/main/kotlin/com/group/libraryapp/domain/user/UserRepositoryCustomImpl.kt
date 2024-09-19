package com.group.libraryapp.domain.user

import com.group.libraryapp.domain.user.QUser.user
import com.group.libraryapp.domain.user.loanhistory.QUserLoanHistory.userLoanHistory
import com.querydsl.jpa.impl.JPAQueryFactory

class UserRepositoryCustomImpl(
  private val queryFactory: JPAQueryFactory,
) : UserRepositoryCustom {

  override fun findAllWithHistories(): List<User> {
    return queryFactory.select(user).distinct()  // Join을 했을 때 같은 User가 중복되어 나오는 것을 방지하기 위해 distinct()를 사용
      .from(user)
      .leftJoin(userLoanHistory).on(userLoanHistory.user.id.eq(user.id)).fetchJoin()  // fetchJoin: n+1 문제를 해결하기 위해 사용
      .fetch()
  }

}