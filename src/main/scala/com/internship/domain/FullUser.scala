package com.internship.domain

case class FullUser(
  login:    String,
  password: String,
  role:     Role,
  email:    String
)
