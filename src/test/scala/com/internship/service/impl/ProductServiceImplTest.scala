package com.internship.service.impl

import cats.effect.{IO, Sync}
import cats.implicits._
import com.internship.dao.ProductDAO
import com.internship.domain.{Product, ProductStatus, Role}
import com.internship.dto.{ProductDto, SmartSearchDto, UserTokenDto}
import com.internship.error.ProductError.RoleNotMatch
import com.internship.service.validation.ProductValidator.ProductValidationError._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec

import java.time.LocalDate

class ProductServiceImplTest extends AnyFreeSpec with MockFactory {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  val product = Product(
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
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validProductId    = "1"
        val validProductDto   = ProductDto("PC1", "2021-10-10", "2021-10-10", "Fast", "100.00", "1", "available")
        val validUserTokenDto = UserTokenDto("Arty", Role.Manager.toString)
        val expected          = Right(1)

        (productDAO.update _).expects(*, *).returning(1.pure[IO]).once()

        val actual = productService.update(validProductId, validProductDto, validUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }

      "update: invalid input" in {
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validProductId    = "1"
        val invalidProductDto = ProductDto("PC1!", "2021-10-10", "2021-10-10", "Fast pc1", "100.00", "1", "available")
        val validUserTokenDto = UserTokenDto("Arty", Role.Manager.toString)
        val expected          = Left(ProductNameFormat)

        (productDAO.update _).expects(*, *).returning(1.pure[IO]).never()

        val actual = productService.update(validProductId, invalidProductDto, validUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "read" - {
      "read: valid input" in {
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validProductId    = "1"
        val validUserTokenDto = UserTokenDto("Arty", "Client")
        val expected          = Right(Some(productService.convertProductToDto(product)))

        (productDAO.read _).expects(*).returning(Some(product).pure[IO]).once()

        val actual = productService.read(validProductId, validUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }

      "read: invalid input" in {
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val invalidProductId  = "a"
        val validUserTokenDto = UserTokenDto("Arty", "Client")
        val expected          = Left(ProductIdFormat)

        (productDAO.read _).expects(*).returning(Some(product).pure[IO]).never()

        val actual = productService.read(invalidProductId, validUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "smartSearch" - {
      "smartSearch: valid" in {
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validSmartSearchDto =
          SmartSearchDto(name = Some("PC1"), None, None, None, None, None, None, None, None, None)
        val expected = Right(Map(1L -> productService.convertProductToDto(product)))

        (productDAO.smartSearch _).expects(*).returning(Map(1L -> product).pure[IO])

        val actual = productService.smartSearch(validSmartSearchDto).unsafeRunSync()

        assert(actual == expected)
      }

      "smartSearch: invalid" in { //no validation => no errors
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validSmartSearchDto =
          SmartSearchDto(name = Some("PC5"), None, None, None, None, None, None, None, None, None)
        val expected = Right(Map.empty[Long, ProductDto])

        (productDAO.smartSearch _).expects(*).returning(Map.empty[Long, Product].pure[IO])

        val actual = productService.smartSearch(validSmartSearchDto).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "readAll" - {
      "readAll: valid" in { //no validation => no errors
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validUserTokenDto = UserTokenDto("Arty", "Client")
        val expected          = Right(Map(1L -> productService.convertProductToDto(product)))

        (productDAO.readAll _).expects().returning(Map(1L -> product).pure[IO]).once()

        val actual = productService.readAll(validUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }

      "readAll: invalid" in { //no validation => no errors
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val invalidUserTokenDto = UserTokenDto("Arty", "BAD_ROLE")
        val expected            = Right(Map(1L -> productService.convertProductToDto(product))) //Left(RoleNotMatch)

        (productDAO.readAll _).expects().returning(Map(1L -> product).pure[IO]).once() //never()

        val actual = productService.readAll(invalidUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "delete" - {
      "delete: valid" in {
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validProductId    = "1"
        val validUserTokenDto = UserTokenDto("Arty", "Manager")
        val expected          = Right(1)

        (productDAO.delete _).expects(*).returning(1.pure[IO]).once()

        val actual = productService.delete(validProductId, validUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }

      "delete: invalid" in {
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validProductId      = "1"
        val invalidUserTokenDto = UserTokenDto("Arty", "Client")
        val expected            = Left(RoleNotMatch())

        (productDAO.delete _).expects(*).returning(1.pure[IO]).never()

        val actual = productService.delete(validProductId, invalidUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "create" - {
      "create: valid" in {
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validUserTokenDto = UserTokenDto("Arty", "Manager")
        val validProductDto   = productService.convertProductToDto(product)
        val expected          = Right(1)

        (productDAO.create _).expects(*).returning(1.pure[IO]).once()

        val actual = productService.create(validProductDto, validUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }

      "create: invalid" in {
        val productDAO     = mock[ProductDAO[IO]]
        val productService = new ProductServiceImpl[IO](productDAO)

        val validUserTokenDto = UserTokenDto("Arty", "Manager")
        val invalidProductDto = productService.convertProductToDto(product.copy(name = "PC1!"))
        val expected          = Left(ProductNameFormat) //fix

        (productDAO.create _).expects(*).returning(1.pure[IO]).never()

        val actual = productService.create(invalidProductDto, validUserTokenDto).unsafeRunSync()

        assert(actual == expected)
      }
    }

  }

}
