package com.internship.service.impl

import cats.effect.{IO, Sync}
import cats.implicits._
import com.internship.dao.ProductDAO
import com.internship.domain.{Product, ProductStatus, Role}
import com.internship.dto.{ProductDto, SmartSearchDto, UserTokenDto}
import com.internship.error.ProductError
import com.internship.error.ProductError.RoleNotMatch
import com.internship.service.validation.ProductValidator.ProductValidationError._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec

import java.time.LocalDate

class ProductServiceImplTest extends AnyFreeSpec with MockFactory {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  val product = Product( //fix validation
    "PC1",
    LocalDate.parse("2021-10-10"),
    LocalDate.parse("2021-10-10"),
    "fast pc1",
    "100.00",
    1,
    ProductStatus.Available
  )

  "Methods tests" - {
    "update:" - {
      "update: valid input" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validProductId    = "1"
        val validProductDto   = ProductDto("Pcc", "2021-10-10", "2021-10-10", "Fast", "100.00", "1", "available")
        val validUserTokenDto = UserTokenDto("Arty", Role.Manager.toString)
        (m.update _).expects(*, *).returning(1.pure[IO]).once()
        val actual   = service.update(validProductId, validProductDto, validUserTokenDto).unsafeRunSync()
        val expected = Right(1)
        assert(actual == expected)
      }

      "update: invalid input" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validProductId    = "1"
        val invalidProductDto = ProductDto("PC1", "2021-10-10", "2021-10-10", "Fast pc1", "100.00", "1", "available")
        val validUserTokenDto = UserTokenDto("Arty", Role.Manager.toString)
        (m.update _).expects(*, *).returning(1.pure[IO]).never()
        val actual   = service.update(validProductId, invalidProductDto, validUserTokenDto).unsafeRunSync()
        val expected = Left(ProductNameFormat)
        assert(actual == expected)
      }
    }

    "read" - {
      "read: valid input" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validProductId    = "1"
        val validUserTokenDto = UserTokenDto("Arty", "Client")
        (m.read _).expects(*).returning(Some(product).pure[IO]).once()
        val actual   = service.read(validProductId, validUserTokenDto).unsafeRunSync()
        val expected = Right(Some(service.convertProductToDto(product)))
        assert(actual == expected)
      }

      "read: invalid input" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val invalidProductId  = "a"
        val validUserTokenDto = UserTokenDto("Arty", "Client")
        (m.read _).expects(*).returning(Some(product).pure[IO]).never()
        val actual   = service.read(invalidProductId, validUserTokenDto).unsafeRunSync()
        val expected = Left(ProductIdFormat)
        assert(actual == expected)
      }
    }

    "smartSearch" - {
      "smartSearch: valid" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validSmartSearchDto =
          SmartSearchDto(name = Some("PC1"), None, None, None, None, None, None, None, None, None)
        (m.smartSearch _).expects(*).returning(Map(1L -> product).pure[IO])
        val actual   = service.smartSearch(validSmartSearchDto).unsafeRunSync()
        val expected = Right(Map(1L -> service.convertProductToDto(product)))
        assert(actual == expected)
      }

      "smartSearch: invalid" in { //no validation => no errors
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validSmartSearchDto =
          SmartSearchDto(name = Some("PC5"), None, None, None, None, None, None, None, None, None)
        (m.smartSearch _).expects(*).returning(Map.empty[Long, Product].pure[IO])
        val actual   = service.smartSearch(validSmartSearchDto).unsafeRunSync()
        val expected = Right(Map.empty[Long, ProductDto])
        assert(actual == expected)
      }
    }

    "readAll" in {}

    "delete" - {
      "delete: valid" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validProductId    = "1"
        val validUserTokenDto = UserTokenDto("Arty", "Manager")
        (m.delete _).expects(*).returning(1.pure[IO]).once()
        val actual   = service.delete(validProductId, validUserTokenDto).unsafeRunSync()
        val expected = Right(1)
        assert(actual == expected)
      }

      "delete: invalid" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validProductId      = "1"
        val invalidUserTokenDto = UserTokenDto("Arty", "Client")
        (m.delete _).expects(*).returning(1.pure[IO]).never()
        val actual   = service.delete(validProductId, invalidUserTokenDto).unsafeRunSync()
        val expected = Left(RoleNotMatch())
        assert(actual == expected)
      }
    }

    "create" - {
      "create: valid" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validUserTokenDto = UserTokenDto("Arty", "Manager")
        val validProductDto   = service.convertProductToDto(product)
        (m.create _).expects(*).returning(1.pure[IO]).once()
        val actual   = service.create(validProductDto, validUserTokenDto).unsafeRunSync()
        val expected = Right(1)
        assert(actual == expected)
      }

      "create: invalid" in {
        val m       = mock[ProductDAO[IO]]
        val service = new ProductServiceImpl[IO](m)

        val validUserTokenDto = UserTokenDto("Arty", "Manager")
        val invalidProductDto = service.convertProductToDto(product)
        (m.create _).expects(*).returning(1.pure[IO]).never()
        val actual   = service.create(invalidProductDto, validUserTokenDto).unsafeRunSync()
        val expected = Left(ProductNameFormat) //fix
        assert(actual == expected)
      }
    }

  }

}
