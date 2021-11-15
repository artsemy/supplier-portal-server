package com.internship.util

import com.internship.HiddenConsatnt.passwordSecretKeyString

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object PasswordUtil {

  val keyBytes  = passwordSecretKeyString.getBytes
  val cipher    = Cipher.getInstance("AES/ECB/PKCS5Padding")
  val secretKey = new SecretKeySpec(keyBytes, "AES")

  def encode(str: String): String = {
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val bytes = cipher.doFinal(str.getBytes("UTF-8"))
    Base64.getEncoder.encodeToString(bytes)
  }

  def decode(str: String): String = {
    val initBytes = Base64.getDecoder.decode(str)
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    val resBytes = cipher.doFinal(initBytes)
    resBytes.map(_.toChar).mkString
  }

}
