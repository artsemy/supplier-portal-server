package com.internship

import com.internship.domain.ProductStatus
import com.internship.util.CaseConversionUtil.snakeToCamel
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions}

object TestClass {
  def main(args: Array[String]): Unit = {
    println("Hi")
//    val key   = "secretKey"
//    val token = Jwt.encode("""{"user":1}""", key, JwtAlgorithm.HS256)
//    // token: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoxfQ.oG3iKnAvj_OKCv0tchT90sv2IFVeaREgvJmwgRcXfkI"
//    val (r1, r2, r3) = Jwt.decodeRawAll(token, key, Seq(JwtAlgorithm.HS256)).get
//    // res1: util.Try[(String, String, String)] = Success(
//    //   value = (
//    //     "{\"typ\":\"JWT\",\"alg\":\"HS256\"}",
//    //     "{\"user\":1}",
//    //     "oG3iKnAvj_OKCv0tchT90sv2IFVeaREgvJmwgRcXfkI"
//    //   )
//    // )
//    Jwt.decodeRawAll(token, "wrongKey", Seq(JwtAlgorithm.HS256))
//    // res2: util.Try[(String, String, String)] = Failure(
//    //   exception = pdi.jwt.exceptions.JwtValidationException: Invalid signature for this token or wrong algorithm.
//    // )
  }
}
