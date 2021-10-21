package com.internship.error

trait UserError extends SupplierPortalError

object UserError {

  final case class UserIdNotFound(id: Long) extends UserError {
    override def message: String = s"user with id: $id not found"
  }

  final case class UserLoginNotFound(login: String) extends UserError {
    override def message: String = s"user with login: $login not found"
  }

}
