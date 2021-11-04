package com.internship.error

trait RoleError extends SupplierPortalError

object RoleError {
  case object RoleNotMatch extends RoleError {
    override def message: String = "role not match"
  }
}
