package com.internship

import cats.effect.{Blocker, ExitCode, IO, IOApp, Resource}

import javax.mail.{Authenticator, Message, PasswordAuthentication, Session, Transport}
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.{Base64, Properties}
import com.internship.HiddenConsatnt._
import com.sun.mail.smtp.SMTPTransport
import fs2.io.tcp.SocketGroup
import fs2.io.tls.TLSContext
import org.bouncycastle.jcajce.provider.symmetric.ChaCha.Base
import org.whispersystems.curve25519.Curve25519

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import scala.io.Source
import scala.util.{Failure, Success, Try}

object Test {

  def main(args: Array[String]): Unit = {
    f1()
//    f2()
  }

  def f1() = {
    val prop = new Properties()
//    prop.put("mail.transport.protocol", "smtps")
    prop.put("mail.smtps.host", "smtp.gmail.com")
    prop.put("mail.smtp.port", "465")
    prop.put("mail.smtp.auth", "true")
    prop.put("mail.smtp.socketFactory.port", "465")
    prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    prop.put("mail.smtp.socketFactory.fallback", "false")
    prop.put("mail.smtps.quitwait", "false")

    val session = Session.getDefaultInstance(prop)

    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(username))
    message.setRecipient(
      Message.RecipientType.TO,
      new InternetAddress("artsemy.k@mail.ru")
    )
    message.setSubject("Testing Gmail SSL")
    message.setText("Dear Mail Crawler," + "\n\n Please do not spam my email!")

    val tr = session.getTransport("smtps")
    Try(tr.connect("smtp.gmail.com", username, password)) match {
      case Failure(exception) => println(exception.getMessage)
      case Success(value)     => println("connected")
    }
    tr.sendMessage(message, message.getAllRecipients)
    tr.close()
    System.out.println("Done")
  }

  def f2() = {
    import java.util.Properties
    import javax.mail.Message
    import javax.mail.Session
    import javax.mail.Transport
    import javax.mail.internet.InternetAddress
    import javax.mail.internet.MimeMessage
    val host = "smtp.gmail.com"
    val from = username
    val to   = "artsemy.k@mail.ru"

    // Get system properties
    val properties = System.getProperties

    // Setup mail server
    properties.setProperty("mail.smtp.host", host)
    properties.setProperty("mail.smtp.port", "465")
    properties.setProperty("mail.smtp.auth", "true")
    properties.setProperty("mail.smtp.socketFactory.port", "465")
    properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")

    // Get the default Session object.
    val session = Session.getInstance(
      properties,
      new Authenticator() {
        override protected def getPasswordAuthentication = new PasswordAuthentication(username, password)
      }
    )

    // Create a default MimeMessage object.
    val message = new MimeMessage(session)

    // Set the RFC 822 "From" header field using the
    // value of the InternetAddress.getLocalAddress method.
    message.setFrom(new InternetAddress(from))

    // Add the given addresses to the specified recipient type.
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))

    // Set the "Subject" header field.
    message.setSubject("hi..!")

    // Sets the given String as this part's content,
    // with a MIME type of "text/plain".
    message.setText("Hi ......")

    // Send message
    Transport.send(message)

    System.out.println("Message Send.....")
  }

}

object Test2 extends IOApp {

  import com.minosiants.pencil.data.{Body, Credentials, Email, Password, Username}
  import com.minosiants.pencil.{pencilLiteralsSyntax, Client}
  import org.typelevel.log4cats.slf4j.Slf4jLogger

  val email = Email.text(
    from"artemiy.konoplaynik@gamil.com",
    to"artsemy.k@mail.ru",
    subject"first email",
    Body.Ascii("hello")
  )

  val logger = Slf4jLogger.getLogger[IO]
  override def run(args: List[String]): IO[ExitCode] = {
    Blocker[IO]
      .use { blocker =>
        SocketGroup[IO](blocker).use { sg =>
          TLSContext.system[IO](blocker).flatMap { tls =>
            val credentials = Credentials(
              Username(username),
              Password(password)
            )
            val client = Client[IO]("smtp.gmail.com", 465, Some(credentials))(blocker, sg, tls, logger)
            val result = client.send(email)
            result.attempt
              .map {
                case Right(value) =>
                  ExitCode.Success
                case Left(error) =>
                  error match {
                    case e: Error     => println(e.getMessage + "1")
                    case e: Throwable => println(e.getMessage + "2")
                  }
                  ExitCode.Error
              }
          }
        }
      }
  }

}

import cats.effect.IO
import tsec.passwordhashers._
import tsec.passwordhashers.jca._
import cats.implicits._

object Test3 {

  def main(args: Array[String]): Unit = {
    val pass:                   Array[Char]                      = Array('h', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd')
    val bestbcryptHash:         IO[PasswordHash[BCrypt]]         = BCrypt.hashpw[IO](pass)
    val bestscryptHash:         IO[PasswordHash[SCrypt]]         = SCrypt.hashpw[IO](pass)
    val besthardenedScryptHash: IO[PasswordHash[HardenedSCrypt]] = HardenedSCrypt.hashpw[IO](pass)

    val bcryptHash:         IO[PasswordHash[BCrypt]]         = BCrypt.hashpw[IO]("hiThere")
    val scryptHash:         IO[PasswordHash[SCrypt]]         = SCrypt.hashpw[IO]("hiThere")
    val hardenedScryptHash: IO[PasswordHash[HardenedSCrypt]] = HardenedSCrypt.hashpw[IO]("hiThere")

    /**
      */
    /** To Validate, you can check against a hash! */
    val checkProgram: IO[Boolean] = for {
      hash  <- bcryptHash
      check <- BCrypt.checkpwBool[IO]("hiThere", hash)
    } yield check

    /** Alternatively if FP is your enemy, you can use the unsafe methods
      */
    val unsafeHash:  PasswordHash[HardenedSCrypt] = HardenedSCrypt.hashpwUnsafe("hiThere")
    val unsafeCheck: Boolean                      = HardenedSCrypt.checkpwUnsafe("hiThere", unsafeHash)
  }

}

object Test4 {
  def main(args: Array[String]): Unit = {
    val curveImpl = Curve25519.getInstance(Curve25519.JAVA)
    val keyPair   = curveImpl.generateKeyPair()
    val message   = "message"
    val sig       = curveImpl.calculateSignature(keyPair.getPrivateKey, message.getBytes)
    println(curveImpl.verifySignature(keyPair.getPublicKey, message.getBytes, sig))
  }
}

object Test5 { //works
  def main(args: Array[String]): Unit = {
    val secretKeyString = "thisisa128bitkey"
    val message         = "1234"
    val keyBytes        = secretKeyString.getBytes
    val cipher          = Cipher.getInstance("AES/ECB/PKCS5Padding")
    val secretKey       = new SecretKeySpec(keyBytes, "AES")

    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val res1 = cipher.doFinal(message.getBytes("UTF-8"))
    println(res1)

    val mk = Base64.getEncoder.encodeToString(res1)
    println(mk)

    val res3 = Base64.getDecoder.decode(mk)
    println(res3)

    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    val res2 = cipher.doFinal(res3)

    println(res2.map(_.toChar).mkString)
  }
}
