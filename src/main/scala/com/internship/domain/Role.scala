package com.internship.domain

sealed trait Role {}

object Role {
  final case object Client extends Role
  final case object Manager extends Role
  final case object Courier extends Role
}
