package com.internship.modules

import cats.effect.Sync
import com.internship.auth.Tokens
import com.internship.service.{OrderService, ProductService, SubscriptionService, TestService, UserService}
import dev.profunktor.redis4cats.RedisCommands

sealed abstract class Services[F[_]](
  val userService:         UserService[F],
  val productService:      ProductService[F],
  val orderService:        OrderService[F],
  val subscriptionService: SubscriptionService[F],
  val testService:         TestService[F]
)

object Services {
  def of[F[_]: Sync](
    repositories: Repositories[F],
    redis:        RedisCommands[F, String, String],
    tokens:       Tokens[F]
  ): Services[F] = {
    new Services[F](
      UserService.of[F](repositories.userDAO),
      ProductService.of[F](repositories.productDAO),
      OrderService.of[F](repositories.orderDAO),
      SubscriptionService.of[F](repositories.subscriptionDAO),
      TestService.of[F](redis, repositories, tokens)
    ) {}
  }
}
