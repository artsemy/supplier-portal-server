package com.internship.modules

import cats.implicits._
import cats.effect.Sync
import com.internship.router.users.{ClientUser, CommonUser, CourierUser, ManagerUser}
import com.internship.router.{OrderRoutes, ProductRoutes, SubscriptionRouter, TestRouter, UserRoutes}
import dev.profunktor.auth.JwtAuthMiddleware
import org.http4s.HttpRoutes

object Routers {
  def of[F[_]: Sync](services: Services[F], security: Security[F]): Routers[F] = {
    new Routers[F](services, security) {}
  }
}

sealed abstract class Routers[F[_]: Sync](services: Services[F], security: Security[F]) {

  private val managerMiddleware = {
    JwtAuthMiddleware[F, ManagerUser](security.managerJwtAuth.value, security.managerAuth.findUser)
  }
  private val clientMiddleware = {
    JwtAuthMiddleware[F, ClientUser](security.clientJwtAuth.value, security.clientAuth.findUser)
  }
  private val courierMiddleware = {
    JwtAuthMiddleware[F, CourierUser](security.courierJwtAuth.value, security.courierAuth.findUser)
  }

  val userRoutes:         HttpRoutes[F] = UserRoutes.routes[F](services.userService)
  val productRoutes:      HttpRoutes[F] = ProductRoutes.routes[F](services.productService)
  val orderRoutes:        HttpRoutes[F] = OrderRoutes.routes[F](services.orderService)
  val subscriptionRoutes: HttpRoutes[F] = SubscriptionRouter.routes[F](services.subscriptionService)

  val testRoutes: HttpRoutes[F] =
    TestRouter.routes[F](services.testService, managerMiddleware, clientMiddleware, courierMiddleware)

  val routes = userRoutes <+> productRoutes <+> orderRoutes <+> subscriptionRoutes <+> testRoutes
}
