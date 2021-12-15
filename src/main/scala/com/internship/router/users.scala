package com.internship.router

import dev.profunktor.auth.jwt._

import com.internship.router.auth.{EncryptedPassword, UserId, UserName}

import java.util.UUID

object users {

  case class ManagerJwtAuth(value: JwtSymmetricAuth)
  case class ClientJwtAuth(value: JwtSymmetricAuth)
  case class CourierJwtAuth(value: JwtSymmetricAuth)

  case class User(id: UserId, name: UserName)

  case class UserWithPassword(id: UserId, name: UserName, password: EncryptedPassword)

  abstract class CommonUser {
    def value: User
  }

  case class ClientUser(value: User) extends CommonUser

  case class ManagerUser(value: User) extends CommonUser

  case class CourierUser(value: User) extends CommonUser

}

object auth {
  case class UserId(value: UUID)

  case class UserName(value: String)

  case class Password(value: String)

  case class EncryptedPassword(value: String)
}
