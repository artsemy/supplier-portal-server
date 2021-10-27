package com.internship.dto

import io.circe.generic.JsonCodec

@JsonCodec
case class SearchDto(exact: List[Pair], period: List[Triple], category: List[Int])
@JsonCodec
case class Pair(typ: String, value: String)
@JsonCodec
case class Triple(typ: String, start: String, end: String)
