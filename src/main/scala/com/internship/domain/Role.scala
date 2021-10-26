package com.internship.domain

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait Role extends EnumEntry

object Role extends Enum[Role] with CirceEnum[Role] {

  val values: IndexedSeq[Role] = findValues

  final case object Client extends Role
  final case object Manager extends Role
  final case object Courier extends Role
}
