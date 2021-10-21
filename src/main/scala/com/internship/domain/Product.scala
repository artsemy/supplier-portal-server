package com.internship.domain

import java.time.LocalDate

case class Product(
  name:            String,
  publicationDate: LocalDate,
  updateDAte:      LocalDate,
  description:     String,
  price:           String, //fix later
  supplierId:      Long,
  productStatus:   ProductStatus
)
