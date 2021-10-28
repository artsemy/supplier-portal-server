package com.internship.domain

case class Order(
  ownerId:     Long,
  courierId:   Long,
  orderStatus: OrderStatus,
  address:     String
)
