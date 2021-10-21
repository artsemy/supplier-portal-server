package com.internship.domain

sealed trait ProductStatus {}

object ProductStatus {
  final case object InProcessing extends ProductStatus
  final case object Available extends ProductStatus
  final case object NotAvailable extends ProductStatus
}
