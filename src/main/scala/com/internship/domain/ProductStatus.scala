package com.internship.domain

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait ProductStatus extends EnumEntry

object ProductStatus extends Enum[ProductStatus] with CirceEnum[ProductStatus] {

  val values: IndexedSeq[ProductStatus] = findValues

  final case object InProcessing extends ProductStatus
  final case object Available extends ProductStatus
  final case object NotAvailable extends ProductStatus
}
