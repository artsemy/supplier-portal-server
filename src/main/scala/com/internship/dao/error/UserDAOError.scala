package com.internship.dao.error

sealed trait UserDAOError extends RuntimeException {
  def message: String
}

object UserDAOError {

  final case class UserIdNotFound(id: Long) extends UserDAOError {
    override def message: String = s"user with id: $id not found"
  }

  final case class UserLoginNotFound(login: String) extends UserDAOError {
    override def message: String = s"user with login: $login not found"
  }

}
