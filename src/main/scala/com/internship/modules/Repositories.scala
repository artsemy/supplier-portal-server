package com.internship.modules

import cats.effect.Sync
import com.internship.dao.{OrderDAO, ProductDAO, SubscriptionDAO, UserDAO}
import doobie.util.transactor.Transactor

sealed abstract class Repositories[F[_]](
  val userDAO:         UserDAO[F],
  val productDAO:      ProductDAO[F],
  val orderDAO:        OrderDAO[F],
  val subscriptionDAO: SubscriptionDAO[F]
)

object Repositories {
  def of[F[_]: Sync](tx: Transactor[F]): Repositories[F] = {
    new Repositories[F](
      UserDAO.of[F](tx),
      ProductDAO.of[F](tx),
      OrderDAO.of[F](tx),
      SubscriptionDAO.of[F](tx)
    ) {}
  }
}
