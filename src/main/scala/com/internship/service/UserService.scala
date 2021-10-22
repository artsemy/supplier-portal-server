package com.internship.service

import cats.effect.Sync
import com.internship.dao.UserDAO
import com.internship.error.UserError
import com.internship.service.impl.UserServiceImpl

trait UserService[F[_]] {
  def logIn(login:        String, password: String): F[Either[UserError, Boolean]]
  def logOut(tokenExists: Boolean): F[Either[UserError, String]]
  def getRole(id:         Long): F[Either[UserError, String]]
}

object UserService {
  def of[F[_]: Sync](authDAO: UserDAO[F]): UserService[F] = new UserServiceImpl[F](authDAO)
}
