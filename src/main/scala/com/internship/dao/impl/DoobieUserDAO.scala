package com.internship.dao.impl

import cats.Functor
import cats.effect.Bracket
import com.internship.dao.UserDAO
import com.internship.domain.{Role, User}
import com.internship.dao.impl.meta.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor

class DoobieUserDAO[F[_]: Functor: Bracket[*[_], Throwable]](tx: Transactor[F]) extends UserDAO[F] {

  override def getPass(login: String): F[Option[String]] = {
    val fr = fr"select password from users where login = $login"
    fr.query[String]
      .option
      .transact(tx)
  }

  override def getRole(id: Long): F[Option[Role]] = {
    val fr = fr"select role from users where id = $id"
    fr.query[Role]
      .option
      .transact(tx)
  }

  override def getUser(login: String, password: String): F[Option[User]] = {
    val fr = fr"select * from users where login = $login and password = $password"
    fr.query[(Long, User)]
      .map { case (_, user) => user }
      .option
      .transact(tx)
  }
}
