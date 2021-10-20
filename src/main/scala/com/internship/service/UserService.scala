package com.internship.service

import cats.effect.Sync
import com.internship.dao.UserDAO
import com.internship.dao.error.UserDAOError
import com.internship.service.impl.UserServiceImpl

trait UserService[F[_]] {
  def logIn(login: String, password: String): F[Either[UserDAOError, Boolean]]
  def logOut(): F[Boolean]
  def getRole(id: Long): F[Either[UserDAOError, String]]
}

object UserService {
  def of[F[_]: Sync](authDAO: UserDAO[F]): UserService[F] = new UserServiceImpl[F](authDAO)
}
