package com.internship

import cats.effect._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import io.circe.config.parser

import com.internship.conf.app._
import com.internship.context.AppContext

import scala.concurrent.ExecutionContext

object SupplierPortalServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    serverResource[IO]
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private def serverResource[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, Server[F]] = for {
    conf    <- Resource.eval(parser.decodePathF[F, AppConf]("app"))
    httpApp <- AppContext.setUp[F](conf)

    server <- BlazeServerBuilder[F](ExecutionContext.global)
      .bindHttp(conf.server.port, conf.server.host)
      .withHttpApp(httpApp)
      .resource

  } yield server
}

/*
curl -XGET "localhost:9000/portal/logIn" -H "Content-Type: application/json" -d "{\"login\": \"arty5\", \"password\": \"1234\"}"
curl "localhost:9000/portal/logOut" -H "loginToken: $token$"

//token arty5 - manager
create
curl -XPOST "localhost:9000/portal/product/create" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI" -H "Content-Type: application/json" -d "{\"name\": \"Pccc\", \"publicationDate\": \"2010-10-10\", \"updateDate\": \"2010-10-10\", \"description\": \"Descr\", \"price\": \"123.00\", \"supplierId\": \"2\", \"productStatus\": \"AVAILABLE\"}"
curl "localhost:9000/portal/product/read/3" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
 */
