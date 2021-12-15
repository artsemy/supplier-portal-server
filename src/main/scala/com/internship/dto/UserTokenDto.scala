package com.internship.dto

import com.internship.domain.FullUser
import io.circe.generic.JsonCodec

@JsonCodec
case class UserTokenDto(login: String = "", role: String = "") {
  def fromUser(user: FullUser): UserTokenDto = {
    UserTokenDto(user.login, user.role.toString)
  }
}
