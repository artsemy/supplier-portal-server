package com.internship

import com.internship.domain.ProductStatus
import com.internship.dto.SearchDto
import com.internship.service.search.SearchParsing
import com.internship.util.CaseConversionUtil.snakeToCamel
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions}

object TestClass {
  def main(args: Array[String]): Unit = {
    println("Hi")
    val l = List(1, 2, 3).map(x => x.toString).reduce(_ + ", " + _)
    println(l)
  }
}

object er {

  def token(): Unit = {
    val key   = "secretKey"
    val token = Jwt.encode("""{"user":1}""", key, JwtAlgorithm.HS256)
    // token: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoxfQ.oG3iKnAvj_OKCv0tchT90sv2IFVeaREgvJmwgRcXfkI"
    val (r1, r2, r3) = Jwt.decodeRawAll(token, key, Seq(JwtAlgorithm.HS256)).get
    // res1: util.Try[(String, String, String)] = Success(
    //   value = (
    //     "{\"typ\":\"JWT\",\"alg\":\"HS256\"}",
    //     "{\"user\":1}",
    //     "oG3iKnAvj_OKCv0tchT90sv2IFVeaREgvJmwgRcXfkI"
    //   )
    // )
    Jwt.decodeRawAll(token, "wrongKey", Seq(JwtAlgorithm.HS256))
    // res2: util.Try[(String, String, String)] = Failure(
    //   exception = pdi.jwt.exceptions.JwtValidationException: Invalid signature for this token or wrong algorithm.
    // )
  }

  def searchJson(): Unit = {
    import io.circe.generic.auto._
    import io.circe._
    import io.circe.generic.JsonCodec
    import io.circe.parser._
    import io.circe.syntax._
    val str =
      """
        |{"exact":[{"typ":"name","value":"PC"},{"typ":"description","value":"fast"}],"period":[{"typ":"publication_date","start":"2020-10-10","end":"2022-10-10"},{"typ":"update_date","start":"2020-10-10","end":"2022-10-10"}],"category":[1, 2]}
        |""".stripMargin
//    case class SearchD(exact: List[Pair], period: List[Triple], category: List[Int])
//    case class Pair(typ: String, value: String)
//    case class Triple(typ: String, start: String, end: String)
    val dec: Either[Error, SearchDto] = decode[SearchDto](str)
    println(dec)
    /*
    example search json
    val str =
      """
        |{
        |  "exact": [
        |    {"typ": "name", "value": "PC"},
        |    {"typ": "description", "value": "fast"}
        |  ],
        |  "period": [
        |    {"typ": "publication_date", "start": "2020-10-10", "end": "2022-10-10"},
        |    {"typ": "update_date", "start": "2020-10-10", "end": "2022-10-10"}
        |  ],
        |  "category": [1, 2]
        |}
        |""".stripMargin
     */
    for {
      dto <- dec
      s    = SearchParsing.parse(dto)
      _    = println(s)
    } yield ()

  }
}
