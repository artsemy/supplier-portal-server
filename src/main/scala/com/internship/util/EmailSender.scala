package com.internship.util

import com.internship.HiddenConsatnt.{password, username}

import javax.mail.{Authenticator, Message, PasswordAuthentication, Session, Transport}
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import scala.concurrent.Await
//v2
import courier.{Envelope, Mailer, Text}
import courier._
import scala.concurrent.duration._
import courier.Defaults.executionContext

object EmailSender {

  def sendEmail(from: String = username, to: String, subject: String, text: String): Unit = {
    // Get system properties
    val properties = System.getProperties

    // Setup mail server
    properties.setProperty("mail.smtp.host", "smtp.gmail.com")
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
    message.setSubject(subject)

    // Sets the given String as this part's content,
    // with a MIME type of "text/plain".
    message.setText(text)

    // Send message
    Transport.send(message)

//    System.out.println("Message Send.....")//log
  }

  //not used
  def sendEmailV2(from: String = username, to: String, subject: String, text: String): Unit = {
    val mailer = Mailer("smtp.gmail.com", 587)
      .auth(true)
      .as(username, password)
      .startTls(true)()
    val future = mailer(
      Envelope
        .from(from.addr)
        .to(to.addr)
        .subject(subject)
        .content(Text(text))
    )
    Await.ready(future, 5.seconds)
  }

}
