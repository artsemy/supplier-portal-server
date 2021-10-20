package com.internship.service.impl

import cats.implicits._
import cats.Monad
import cats.data.EitherT
import com.internship.dao.UserDAO
import com.internship.service.UserService
import com.internship.dao.error.UserDAOError

class UserServiceImpl[F[_]: Monad](authDAO: UserDAO[F]) extends UserService[F] {

  override def logIn(login: String, password: String): F[Either[UserDAOError, Boolean]] = {
    val optPass = authDAO.getPass(login)
    EitherT
      .fromOptionF(optPass, UserDAOError.UserLoginNotFound(login))
      .value
      .map(x => x.map(y => y == password))
  }

  override def logOut(): F[Boolean] = {
    true.pure[F]
  } //remove id from session???

  override def getRole(id: Long): F[Either[UserDAOError, String]] = {
    val optRole = authDAO.getRole(id)
    EitherT
      .fromOptionF(optRole, UserDAOError.UserIdNotFound(id))
      .value
      .map(x => x.map(_.toString))
  }

}
