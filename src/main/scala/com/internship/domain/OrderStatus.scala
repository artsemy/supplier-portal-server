package com.internship.domain

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait OrderStatus extends EnumEntry

object OrderStatus extends Enum[OrderStatus] with CirceEnum[OrderStatus] {

  val values: IndexedSeq[OrderStatus] = findValues

  final case object inProcessing extends OrderStatus
  final case object Ordered extends OrderStatus
  final case object Assigned extends OrderStatus
  final case object Delivered extends OrderStatus
}
