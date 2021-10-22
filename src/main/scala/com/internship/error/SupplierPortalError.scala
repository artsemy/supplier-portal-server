package com.internship.error

trait SupplierPortalError extends Throwable {
  def message: String
}

object SupplierPortalError {}
