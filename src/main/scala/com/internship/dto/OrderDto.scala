package com.internship.dto

import io.circe.generic.JsonCodec

@JsonCodec
case class OrderDto(
  ownerId:     String,
  courierId:   String,
  orderStatus: String,
  address:     String
)
