package com.internship.service

import cats.effect.Sync
import com.internship.dao.UserDAO
import com.internship.error.UserError
import com.internship.dto.{AuthDto, UserTokenDto}
import com.internship.service.impl.UserServiceImpl
import io.chrisdavenport.log4cats.Logger
//import org.typelevel.log4cats.Logger
import org.http4s.Headers

trait UserService[F[_]] {
  def logIn(authDto:            AuthDto):           F[Either[UserError, String]]
  def logOut(tokenExists:       Boolean):           F[Either[UserError, String]]
  def generateToken(authDto:    AuthDto):           F[String]
  def decodeToken(token:        String): F[Either[UserError, UserTokenDto]]
  def subscribeSupplier(userId: String, supplierId: String): F[Either[UserError, Int]]
  def subscribeCategory(userId: String, categoryId: String): F[Either[UserError, Int]]
}

object UserService {
  def of[F[_]: Sync: Logger](authDAO: UserDAO[F]): UserService[F] = new UserServiceImpl[F](authDAO)
}
