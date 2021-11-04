package com.internship.service

import cats.effect.Sync
import com.internship.dao.UserDAO
import com.internship.domain.User
import com.internship.error.UserError
import com.internship.dto.AuthDto
import com.internship.service.impl.UserServiceImpl

trait UserService[F[_]] {
  def logIn(authDto:      AuthDto): F[Either[UserError, User]]
  def logOut(tokenExists: Boolean): F[Either[UserError, String]]
}

object UserService {
  def of[F[_]: Sync](authDAO: UserDAO[F]): UserService[F] = new UserServiceImpl[F](authDAO)
}
