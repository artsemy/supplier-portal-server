package com.internship.router.dto

import io.circe.generic.JsonCodec

@JsonCodec
final case class AuthDto(login: String, password: String)
