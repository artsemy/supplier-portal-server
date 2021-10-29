package com.internship.dao

import cats.effect.Sync
import com.internship.dao.impl.DoobieUserDAO
import com.internship.domain.{Role, User}
import doobie.util.transactor.Transactor

trait UserDAO[F[_]] {
  def getPass(login:            String):          F[Option[String]]
  def getRole(id:               Long): F[Option[Role]]
  def getUser(login:            String, password: String): F[Option[User]]
  def subscribeSupplier(userId: Long, supplierId: Long): F[Int]
  def subscribeCategory(userId: Long, categoryId: Long): F[Int]
}

object UserDAO {
  def of[F[_]: Sync](tx: Transactor[F]): DoobieUserDAO[F] = new DoobieUserDAO[F](tx)
}
