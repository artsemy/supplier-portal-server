package com.internship

import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import com.internship.constant.ConstantStrings.LOGIN_HEADER_TOKEN
import com.internship.domain.{OrderProduct, OrderStatus}
import com.internship.dto.{AuthDto, OrderDto, ProductDto, SmartSearchDto}
import org.http4s._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.io._

import scala.concurrent.ExecutionContext

object UserClient extends IOApp {

  private val uri = uri"http://localhost:9000/portal/user"
  private def printLine(string: String = ""): IO[Unit] = IO(println(string))

  override def run(args: List[String]): IO[ExitCode] = {

    val authDto = AuthDto("arty1", "1234")

    BlazeClientBuilder[IO](ExecutionContext.global).resource
      .use { client =>
        for {
          _ <- printLine("valid input")

          _ <- client.expect[String](Method.GET(authDto, uri / "logIn")) >>= printLine
          _ <- client.expect[String](Method.GET(uri / "logOut", Header(LOGIN_HEADER_TOKEN, "some token"))) >>= printLine

          _ <- printLine("invalid input")

          _ <- client.expect[String](Method.GET(authDto.copy(login = "barty"), uri / "logIn")) >>= printLine
          _ <- client.expect[String](Method.GET(uri / "logOut")) >>= printLine
        } yield ()
      }
      .as(ExitCode.Success)
  }

}

object SubscriptionClient extends IOApp {

  private val uri = uri"http://localhost:9000/portal/subs"
  private def printLine(string: String = ""): IO[Unit] = IO(println(string))

  override def run(args: List[String]): IO[ExitCode] = {

    val userId     = "1"
    val categoryId = "1"
    val supplierId = "1"

    BlazeClientBuilder[IO](ExecutionContext.global).resource
      .use { client =>
        for {
          _ <- printLine("valid input")

          _ <- printLine("category")
          _ <- client
            .expect[Map[Long, String]](Method.GET(uri / "all_cat" / userId))
            .flatMap(x => printLine("all subs empty: " + x.toString()))
          _ <- client
            .expect[Int](Method.POST(uri / "add_cat" / userId / categoryId))
            .flatMap(x => printLine("new subs one: " + x.toString))
          _ <- client
            .expect[Map[Long, String]](Method.GET(uri / "all_cat" / userId))
            .flatMap(x => printLine("all subs one: " + x.toString()))
          _ <- client
            .expect[Int](Method.POST(uri / "remove_cat" / userId / categoryId))
            .flatMap(x => printLine("removed subs one:" + x.toString))
          _ <- client
            .expect[Map[Long, String]](Method.GET(uri / "all_sup" / userId))
            .flatMap(x => printLine("all subs empty: " + x.toString()))

          _ <- printLine("supplier")
          _ <- client
            .expect[Map[Long, String]](Method.GET(uri / "all_sup" / userId))
            .flatMap(x => printLine("all subs empty: " + x.toString()))
          _ <- client
            .expect[Int](Method.POST(uri / "add_sup" / userId / supplierId))
            .flatMap(x => printLine("new subs one: " + x.toString))
          _ <- client
            .expect[Map[Long, String]](Method.GET(uri / "all_sup" / userId))
            .flatMap(x => printLine("all subs one: " + x.toString()))
          _ <- client
            .expect[Int](Method.POST(uri / "remove_sup" / userId / supplierId))
            .flatMap(x => printLine("removed subs one:" + x.toString))
          _ <- client
            .expect[Map[Long, String]](Method.GET(uri / "all_sup" / userId))
            .flatMap(x => printLine("all subs empty: " + x.toString()))

          _ <- printLine("invalid input")

          _ <- client.expect[String](Method.GET(uri / "all_cat" / userId.map(_ => 'a'))) >>= printLine
          _ <- client.expect[String](Method.POST(uri / "remove_sup" / userId / supplierId.map(_ => 'a'))) >>= printLine
          _ <- client.expect[String](Method.POST(uri / "remove_cat" / userId / categoryId.map(_ => 'a'))) >>= printLine
        } yield ()
      }
      .as(ExitCode.Success)
  }

}

object ProductClient extends IOApp {

  private val uri = uri"http://localhost:9000/portal/product"
  private def printLine(string: String = ""): IO[Unit] = IO(println(string))

  override def run(args: List[String]): IO[ExitCode] = {

    val managerToken =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
    val clientToken =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHkxIiwicm9sZSI6IkNsaWVudCJ9.qFO-jV5m-866TDwe-qTkIjQ8uTLjy_6_8IlDwFHVVNw"
    val vpDto  = ProductDto("PC10", "2021-11-05", "2021-11-05", "New prod", "321.00", "1", "available")
    val ivpDto = vpDto.copy(name = "PC!")
    val sDto   = SmartSearchDto(Some("PC"), None, None, None, None, None, None, None, None, None)

    BlazeClientBuilder[IO](ExecutionContext.global).resource
      .use { client =>
        for {
          _ <- printLine("valid input")

          id <- client
            .expect[Int](Method.POST(vpDto, uri / "create", Header(LOGIN_HEADER_TOKEN, managerToken)))
          _ <- printLine(s"create id: $id")
          _ <- client
            .expect[Int](
              Method
                .POST(
                  vpDto.copy(price = "1.00"),
                  uri / "update" / id.toString,
                  Header(LOGIN_HEADER_TOKEN, managerToken)
                )
            )
            .flatMap(x => printLine("update one: " + x.toString))
          _ <- client
            .expect[Option[ProductDto]](
              Method.GET(uri / "read" / id.toString, Header(LOGIN_HEADER_TOKEN, managerToken))
            )
            .flatMap(x => printLine("read: " + x.toString))
          _ <- client
            .expect[Int](Method.POST(uri / "delete" / id.toString, Header(LOGIN_HEADER_TOKEN, managerToken)))
            .flatMap(x => printLine("delete one: " + x.toString))
          _ <- client
            .expect[Map[Long, ProductDto]](Method.GET(uri / "read_all", Header(LOGIN_HEADER_TOKEN, managerToken)))
            .flatMap(x => printLine("all prod: " + x.map(y => "\n" + y.toString())))
          _ <- client
            .expect[Map[Long, ProductDto]](
              Method.GET(sDto, uri / "smart_search", Header(LOGIN_HEADER_TOKEN, managerToken))
            )
            .flatMap(x => printLine("search 4: " + x.map(y => "\n" + y.toString())))

          _ <- printLine("invalid input")

          _ <- client
            .expect[String](Method.POST(ivpDto, uri / "create", Header(LOGIN_HEADER_TOKEN, managerToken))) >>= printLine
          _ <- client
            .expect[String](Method.POST(ivpDto, uri / "create", Header(LOGIN_HEADER_TOKEN, clientToken))) >>= printLine
          _ <- client
            .expect[String](
              Method.POST(ivpDto, uri / "create", Header(LOGIN_HEADER_TOKEN, managerToken.tail))
            ) >>= printLine

        } yield ()
      }
      .as(ExitCode.Success)
  }

}

object OrderClient extends IOApp {

  private val uri = uri"http://localhost:9000/portal/order"
  private def printLine(string: String = ""): IO[Unit] = IO(println(string))

  override def run(args: List[String]): IO[ExitCode] = {

    val orderDto      = OrderDto("5", "0", "inProcessing", "address5")
    val newOrderDto   = orderDto.copy(address = "NEW ADDRESS")
    val productId     = "4"
    val amount        = "4"
    val newAmount     = "5"
    val nextStatus    = OrderStatus.Ordered
    val iOrderDto     = orderDto.copy(orderStatus = "badStat")
    val notNextStatus = OrderStatus.Delivered

    BlazeClientBuilder[IO](ExecutionContext.global).resource
      .use { client =>
        for {
          _ <- printLine("valid input")

          orderId <- client.expect[Int](Method.POST(orderDto, uri / "create"))
          _       <- printLine("create id: " + orderId.toString)
          _ <- client
            .expect[Option[OrderDto]](Method.GET(uri / "read" / orderId.toString))
            .flatMap(x => printLine("read: " + x.toString))
          _ <- client
            .expect[Int](Method.POST(newOrderDto, uri / "update" / orderId.toString))
            .flatMap(x => printLine("update one: " + x.toString))
          orderProductId <- client
            .expect[Int](Method.POST(uri / "add_product" / orderId.toString / productId / amount))
          _ <- printLine("add product to order one: " + orderProductId.toString)
          _ <- client
            .expect[Map[Long, OrderProduct]](Method.GET(uri / "all_product" / orderId.toString))
            .flatMap(x => printLine("read all: " + x.toString()))
          _ <- client
            .expect[Int](Method.POST(uri / "update_amount" / orderProductId.toString / newAmount))
            .flatMap(x => printLine("amount update one: " + x.toString))
          _ <- client
            .expect[Int](Method.POST(uri / "remove_product" / orderId.toString / productId))
            .flatMap(x => printLine("remove product to order one: " + x.toString))
          _ <- client
            .expect[Int](Method.POST(orderDto, uri / "set_status" / orderId.toString / nextStatus.toString))
            .flatMap(x => printLine("change status one: " + x.toString))
          _ <- client
            .expect[Int](Method.POST(uri / "delete" / orderId.toString))
            .flatMap(x => printLine("delete one: " + x.toString))

          _ <- printLine("invalid input")

          _ <- client.expect[String](Method.POST(iOrderDto, uri / "create")) >>= printLine

          iOrderId <- client.expect[Int](Method.POST(orderDto, uri / "create"))
          _ <- client
            .expect[String](
              Method.POST(orderDto, uri / "set_status" / iOrderId.toString / notNextStatus.toString)
            ) >>= printLine
          _ <- client
            .expect[Int](Method.POST(uri / "delete" / iOrderId.toString))

        } yield ()
      }
      .as(ExitCode.Success)
  }

}
