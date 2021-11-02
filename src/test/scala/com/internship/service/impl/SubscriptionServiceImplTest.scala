package com.internship.service.impl

import cats.implicits._
import cats.effect.{IO, Sync}
import com.internship.dao.SubscriptionDAO
import com.internship.service.validation.SubscriptionValidator.SubscriptionValidatorError._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec

class SubscriptionServiceImplTest extends AnyFreeSpec with MockFactory {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  "Methods tests" - {
    "readAllSubscriptionSupplier" - {
      "readAllSubscriptionSupplier: valid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId = "1"
        val expected    = Right(Map(1L -> "name"))

        (subDAO.readAllSubscriptionSupplier _).expects(*).returning(Map(1L -> "name").pure[IO]).once()

        val actual = subService.readAllSubscriptionSupplier(validUserId).unsafeRunSync()

        assert(actual == expected)
      }

      "readAllSubscriptionSupplier: invalid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val invalidUserId = "a"
        val expected      = Left(subscriptionUserIdFormat)

        (subDAO.readAllSubscriptionSupplier _).expects(*).returning(Map(1L -> "name").pure[IO]).never()

        val actual = subService.readAllSubscriptionSupplier(invalidUserId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "removeSubscriptionSupplier" - {
      "removeSubscriptionSupplier: valid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId     = "1"
        val validSupplierId = "1"
        val expected        = Right(1)

        (subDAO.removeSubscriptionSupplier _).expects(*, *).returning(1.pure[IO]).once()

        val actual = subService.removeSubscriptionSupplier(validUserId, validSupplierId).unsafeRunSync()

        assert(actual == expected)
      }

      "removeSubscriptionSupplier: invalid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId     = "1"
        val validSupplierId = "a"
        val expected        = Left(subscriptionSupplierIdFormat)

        (subDAO.removeSubscriptionSupplier _).expects(*, *).returning(1.pure[IO]).never()

        val actual = subService.removeSubscriptionSupplier(validUserId, validSupplierId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "addSubscriptionSupplier" - {
      "addSubscriptionSupplier: valid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId     = "1"
        val validSupplierId = "1"
        val expected        = Right(1)

        (subDAO.addSubscriptionSupplier _).expects(*, *).returning(1.pure[IO]).once()

        val actual = subService.addSubscriptionSupplier(validUserId, validSupplierId).unsafeRunSync()

        assert(actual == expected)
      }

      "addSubscriptionSupplier: invalid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId     = "1"
        val validSupplierId = "a"
        val expected        = Left(subscriptionSupplierIdFormat)

        (subDAO.addSubscriptionSupplier _).expects(*, *).returning(1.pure[IO]).never()

        val actual = subService.addSubscriptionSupplier(validUserId, validSupplierId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "readAllSubscriptionCategory" - {
      "readAllSubscriptionCategory: valid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId = "1"
        val expected    = Right(Map(1L -> "name"))

        (subDAO.readAllSubscriptionCategory _).expects(*).returning(Map(1L -> "name").pure[IO]).once()

        val actual = subService.readAllSubscriptionCategory(validUserId).unsafeRunSync()

        assert(actual == expected)
      }

      "readAllSubscriptionCategory: invalid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val invalidUserId = "a"
        val expected      = Left(subscriptionUserIdFormat)

        (subDAO.readAllSubscriptionCategory _).expects(*).returning(Map(1L -> "name").pure[IO]).never()

        val actual = subService.readAllSubscriptionCategory(invalidUserId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "addSubscriptionCategory" - {
      "addSubscriptionCategory: valid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId     = "1"
        val validCategoryId = "1"
        val expected        = Right(1)

        (subDAO.addSubscriptionCategory _).expects(*, *).returning(1.pure[IO]).once()

        val actual = subService.addSubscriptionCategory(validUserId, validCategoryId).unsafeRunSync()

        assert(actual == expected)
      }

      "addSubscriptionCategory: invalid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId     = "1"
        val validCategoryId = "a"
        val expected        = Left(subscriptionCategoryIdFormat)

        (subDAO.addSubscriptionCategory _).expects(*, *).returning(1.pure[IO]).never()

        val actual = subService.addSubscriptionCategory(validUserId, validCategoryId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "removeSubscriptionCategory" - {
      "removeSubscriptionCategory: valid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId     = "1"
        val validCategoryId = "1"
        val expected        = Right(1)

        (subDAO.removeSubscriptionCategory _).expects(*, *).returning(1.pure[IO]).once()

        val actual = subService.removeSubscriptionCategory(validUserId, validCategoryId).unsafeRunSync()

        assert(actual == expected)
      }

      "removeSubscriptionCategory: invalid" in {
        val subDAO     = mock[SubscriptionDAO[IO]]
        val subService = new SubscriptionServiceImpl[IO](subDAO)

        val validUserId     = "1"
        val validCategoryId = "a"
        val expected        = Left(subscriptionCategoryIdFormat)

        (subDAO.removeSubscriptionCategory _).expects(*, *).returning(1.pure[IO]).never()

        val actual = subService.removeSubscriptionCategory(validUserId, validCategoryId).unsafeRunSync()

        assert(actual == expected)
      }
    }

  }

}
