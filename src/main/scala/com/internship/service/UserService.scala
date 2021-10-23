package com.internship.service

import cats.effect.Sync
import com.internship.dao.UserDAO
import com.internship.error.UserError
import com.internship.domain.dto.AuthDto
import com.internship.service.impl.UserServiceImpl
import org.http4s.Headers

trait UserService[F[_]] {
  def logIn(authDto:         AuthDto): F[Either[UserError, String]]
  def logOut(tokenExists:    Boolean): F[Either[UserError, String]]
  def generateToken(authDto: AuthDto): F[String]
}

object UserService {
  def of[F[_]: Sync](authDAO: UserDAO[F]): UserService[F] = new UserServiceImpl[F](authDAO)
}
