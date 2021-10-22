package com.internship.service.impl

import cats.implicits._
import cats.Monad
import cats.data.EitherT
import com.internship.dao.UserDAO
import com.internship.error.UserError
import com.internship.error.UserError._
import com.internship.service.UserService

class UserServiceImpl[F[_]: Monad](authDAO: UserDAO[F]) extends UserService[F] {

  val LOG_OUT_MESSAGE = "logged out"

  override def logIn(login: String, password: String): F[Either[UserError, Boolean]] = {
    val optPass = authDAO.getPass(login)
    EitherT
      .fromOptionF(optPass, UserError.UserLoginNotFound(login))
      .value
      .map(x => x.map(y => y == password))
  }

  override def logOut(tokenExists: Boolean): F[Either[UserError, String]] = {
    val res: Either[UserError, String] =
      if (tokenExists)
        Right(LOG_OUT_MESSAGE)
      else
        Left(UserCantLogOut())
    res.pure[F]
  }

  override def getRole(id: Long): F[Either[UserError, String]] = {
    val optRole = authDAO.getRole(id)
    EitherT
      .fromOptionF(optRole, UserError.UserIdNotFound(id))
      .value
      .map(x => x.map(_.toString))
  }

}
