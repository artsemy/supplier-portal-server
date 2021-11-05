package com.internship.domain

import io.circe.generic.JsonCodec

@JsonCodec
case class OrderProduct(
  name:   String,
  amount: Int
)
