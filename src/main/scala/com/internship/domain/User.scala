package com.internship.domain

case class User(
  login:    String,
  password: String,
  role:     Role,
  email:    String
)
