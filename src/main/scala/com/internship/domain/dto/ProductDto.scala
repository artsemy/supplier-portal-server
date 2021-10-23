package com.internship.domain.dto

import io.circe.generic.JsonCodec

@JsonCodec
final case class ProductDto(
  name:            String,
  publicationDate: String,
  updateDate:      String,
  description:     String,
  price:           String,
  supplierId:      String,
  productStatus:   String
)
