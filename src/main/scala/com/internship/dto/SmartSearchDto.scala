package com.internship.dto

import io.circe.generic.JsonCodec

@JsonCodec
case class SmartSearchDto(
  name:           Option[String],
  pubDate:        Option[String],
  upDate:         Option[String],
  description:    Option[String],
  price:          Option[String],
  supplierId:     Option[String],
  productStatus:  Option[String],
  pubDatePeriod:  Option[(String, String)],
  upDatePeriod:   Option[(String, String)],
  listCategoryId: Option[List[Int]]
)

//price period ignore because of type, fix much later
