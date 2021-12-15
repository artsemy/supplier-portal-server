package com.internship.service

import cats._
import cats.syntax.all._
import cats.{Applicative, Functor}
import com.internship.dao.UserDAO
import com.internship.domain.Role
import com.internship.domain.Role._
import com.internship.router.auth.{UserId, UserName}
import com.internship.router.users.{ClientUser, CommonUser, CourierUser, ManagerUser, User}
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.RedisCommands
import pdi.jwt.JwtClaim
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.parser.decode
import io.circe.syntax._

import java.util.UUID

trait UsersAuth[F[_], A] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
}

object UsersAuth {

  def manager[F[_]: Applicative](userDAO: UserDAO[F]): UsersAuth[F, ManagerUser] = new UsersAuth[F, ManagerUser] {
    override def findUser(token: JwtToken)(claim: JwtClaim): F[Option[ManagerUser]] = {
      Option(ManagerUser(User(UserId(UUID.randomUUID()), UserName("name")))).pure[F] //fix later
    }
  }

  def client[F[_]: Functor](redis: RedisCommands[F, String, String]): UsersAuth[F, ClientUser] =
    new UsersAuth[F, ClientUser] {
      override def findUser(token: JwtToken)(claim: JwtClaim): F[Option[ClientUser]] = {
        find(redis, token, Client).asInstanceOf[F[Option[ClientUser]]]
      }
    }

  def courier[F[_]: Functor](redis: RedisCommands[F, String, String]): UsersAuth[F, CourierUser] =
    new UsersAuth[F, CourierUser] {
      override def findUser(token: JwtToken)(claim: JwtClaim): F[Option[CourierUser]] = {
        find(redis, token, Courier).asInstanceOf[F[Option[CourierUser]]]
      }
    }

  private def find[F[_]: Functor](redis: RedisCommands[F, String, String], token: JwtToken, role: Role) = {
    redis.get(token.value).map {
      _.flatMap { u =>
        decode[User](u).toOption match {
          case Some(user) => Option(makeUser(role, user))
          case None       => None
        }
      }
    }
  }

  //user type problems
  private def makeUser(role: Role, user: User): CommonUser = {
    role match {
      case Courier => CourierUser(user)
      case Client  => ClientUser(user)
      case Manager => ManagerUser(user)
    }
  }

}
