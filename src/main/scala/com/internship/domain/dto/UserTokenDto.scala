package com.internship.domain.dto

import com.internship.domain.User
import io.circe.generic.JsonCodec

@JsonCodec
case class UserTokenDto(login: String = "", role: String = "") {
  def fromUser(user: User): UserTokenDto = {
    UserTokenDto(user.login, user.role.toString)
  }
}
