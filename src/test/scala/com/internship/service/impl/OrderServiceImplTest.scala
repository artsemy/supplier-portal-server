package com.internship.service.impl

import cats.implicits._
import cats.effect.{IO, Sync}
import com.internship.dao.OrderDAO
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import com.internship.domain.{Order, OrderProduct, OrderStatus}
import com.internship.service.validation.OrderValidator.OrderValidationError._
import com.internship.util.ConverterToDto.convertOrderToDto
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

class OrderServiceImplTest extends AnyFreeSpec with MockFactory {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]
  val order = Order(1, 0, OrderStatus.Ordered, "address1")

  "Methods tests" - {
    "update" - {
      "update: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId  = "1"
        val validOrderDto = convertOrderToDto(order)
        val expected      = Right(1)

        (orderDAO.update _).expects(*, *).returning(1.pure[IO]).once()

        val actual = orderService.update(validOrderId, validOrderDto).unsafeRunSync()

        assert(actual == expected)
      }

      "update: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val invalidOrderId = "a"
        val validOrderDto  = convertOrderToDto(order)
        val expected       = Left(OrderIdFormat)

        (orderDAO.update _).expects(*, *).returning(1.pure[IO]).never()

        val actual = orderService.update(invalidOrderId, validOrderDto).unsafeRunSync()

        assert(actual == expected)
      }

    }

    "delete" - {
      "delete: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId = "1"
        val expected     = Right(1)

        (orderDAO.delete _).expects(*).returning(1.pure[IO]).once()

        val actual = orderService.delete(validOrderId).unsafeRunSync()

        assert(actual == expected)
      }

      "delete: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId = "a"
        val actual       = orderService.delete(validOrderId).unsafeRunSync()

        (orderDAO.delete _).expects(*).returning(1.pure[IO]).never()

        val expected = Left(OrderIdFormat)

        assert(actual == expected)
      }
    }

    "readAllProductInOrder" - {
      "readAllProductInOrder: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId = "1"
        val expected     = Right(Map(1L -> OrderProduct("name", 1)))

        (orderDAO.readAllProductInOrder _).expects(*).returning(Map(1L -> OrderProduct("name", 1)).pure[IO]).once()

        val actual = orderService.readAllProductInOrder(validOrderId).unsafeRunSync()

        assert(actual == expected)
      }

      "readAllProductInOrder: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val invalidOrderId = "a"
        val expected       = Left(OrderIdFormat)

        (orderDAO.readAllProductInOrder _).expects(*).returning(Map(1L -> OrderProduct("name", 1)).pure[IO]).never()

        val actual = orderService.readAllProductInOrder(invalidOrderId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "read" - {
      "read: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId = "1"
        val expected     = Right(Some(convertOrderToDto(order)))

        (orderDAO.read _).expects(*).returning(Some(order).pure[IO]).once()

        val actual = orderService.read(validOrderId).unsafeRunSync()

        assert(actual == expected)
      }

      "read: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val invalidOrderId = "a"
        val expected       = Left(OrderIdFormat)

        (orderDAO.read _).expects(*).returning(Some(order).pure[IO]).never()

        val actual = orderService.read(invalidOrderId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "changeStatus" - {
      "changeStatus: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId     = "1"
        val validOrderDto    = convertOrderToDto(order)
        val validOrderStatus = "assigned"
        val expected         = Right(1)

        (orderDAO.update _).expects(*, *).returning(1.pure[IO]).once()

        val actual = orderService.changeStatus(validOrderId, validOrderDto, validOrderStatus).unsafeRunSync()

        assert(actual == expected)
      }

      "changeStatus: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId       = "1"
        val validOrderDto      = convertOrderToDto(order)
        val invalidOrderStatus = "delivered"
        val expected           = Left(OrderNextStatusFormat)

        (orderDAO.update _).expects(*, *).returning(1.pure[IO]).never()

        val actual = orderService.changeStatus(validOrderId, validOrderDto, invalidOrderStatus).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "removeProduct" - {
      "removeProduct: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId   = "1"
        val validProductId = "1"
        val expected       = Right(1)

        (orderDAO.removeProduct _).expects(*, *).returning(1.pure[IO]).once()

        val actual = orderService.removeProduct(validOrderId, validProductId).unsafeRunSync()

        assert(actual == expected)
      }

      "removeProduct: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId   = "1"
        val validProductId = "a"
        val expected       = Left(OrderProductIdFormat)

        (orderDAO.removeProduct _).expects(*, *).returning(1.pure[IO]).never()

        val actual = orderService.removeProduct(validOrderId, validProductId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "updateProductAmount" - {
      "updateProductAmount: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderProductId = "1"
        val validAmount         = "1"
        val expected            = Right(1)

        (orderDAO.updateProductAmount _).expects(*, *).returning(1.pure[IO]).once()

        val actual = orderService.updateProductAmount(validOrderProductId, validAmount).unsafeRunSync()

        assert(actual == expected)
      }

      "updateProductAmount: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderProductId = "1"
        val validAmount         = "a"
        val expected            = Left(OrderProductAmountFormat)

        (orderDAO.updateProductAmount _).expects(*, *).returning(1.pure[IO]).never()

        val actual = orderService.updateProductAmount(validOrderProductId, validAmount).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "addProduct" - {
      "addProduct: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderId   = "1"
        val validProductId = "1"
        val validAmount    = "4"
        val expected       = Right(1)

        (orderDAO.addProduct _).expects(*, *, *).returning(1.pure[IO]).once()

        val actual = orderService.addProduct(validOrderId, validProductId, validAmount).unsafeRunSync()

        assert(actual == expected)
      }

      "addProduct: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val invalidOrderId = "a"
        val validProductId = "1"
        val validAmount    = "4"
        val expected       = Left(OrderIdFormat)

        (orderDAO.addProduct _).expects(*, *, *).returning(1.pure[IO]).never()

        val actual = orderService.addProduct(invalidOrderId, validProductId, validAmount).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "create" - {
      "create: valid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val validOrderDto = convertOrderToDto(order)
        val expected      = Right(1)

        (orderDAO.create _).expects(*).returning(1.pure[IO]).once()

        val actual = orderService.create(validOrderDto).unsafeRunSync()

        assert(actual == expected)
      }

      "create: invalid" in {
        val orderDAO     = mock[OrderDAO[IO]]
        val orderService = new OrderServiceImpl[IO](orderDAO)

        val invalidOrderDto = convertOrderToDto(order).copy(orderStatus = "badStatus")
        val expected        = Left(OrderStatusFormat)

        (orderDAO.create _).expects(*).returning(1.pure[IO]).never()

        val actual = orderService.create(invalidOrderDto).unsafeRunSync()

        assert(actual == expected)
      }
    }

  }

}
