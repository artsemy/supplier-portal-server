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

// curl -XGET "localhost:9000/portal/logIn" -H "Content-Type: application/json" -d "{\"login\": \"arty1\", \"password\": \"1234\"}"
// curl "localhost:9000/portal/logOut"
