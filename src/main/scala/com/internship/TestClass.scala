package com.internship

import cats.effect.IO
import com.internship.domain.ProductStatus
import com.internship.error.ProductError
import com.internship.util.CaseConversionUtil.snakeToCamel
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions}
import cats.implicits._

object TestClass {
  def main(args: Array[String]): Unit = {
    println("Hi")
//    er.search()
    val r = Right((1, 1))
    val p = r.traverse { case (i, i1) => IO(1) }
    println(p.unsafeRunSync())
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

  def search(): Unit = {
    import io.circe.generic.auto._
    import io.circe._
    import io.circe.generic.JsonCodec
    import io.circe.parser._
    import io.circe.syntax._
    val str =
      """
        |{
        |  "name": ["Arty", "Barty", "Carty"]
        |}
        |""".stripMargin
    case class Pair(name: Option[(String, String)], last: Option[String])
    val dec: Either[Error, Pair] = decode[Pair](str)
    println(dec)
  }

}
