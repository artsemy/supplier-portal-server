package com.internship.service.impl

import cats.implicits._
import cats.effect.{IO, Sync}
import com.internship.dao.UserDAO
import com.internship.domain.{Role, User}
import com.internship.dto.AuthDto
import com.internship.error.UserError._
import com.internship.service.validation.UserValidator.UserValidationError._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec

class UserServiceImplTest extends AnyFreeSpec with MockFactory {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  "Methods tests" - {
    "subscribeCategory" - {
      "subscribeCategory: valid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val validUserId     = "1"
        val validCategoryId = "1"
        val expected        = Right(1)

        (userDAO.subscribeCategory _).expects(*, *).returning(1.pure[IO]).once()

        val actual = userService.subscribeCategory(validUserId, validCategoryId).unsafeRunSync()

        assert(expected == actual)
      }

      "subscribeCategory: invalid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val validUserId       = "1"
        val invalidCategoryId = "a"
        val expected          = Left(CategoryIdFormat)

        (userDAO.subscribeCategory _).expects(*, *).returning(1.pure[IO]).never()

        val actual = userService.subscribeCategory(validUserId, invalidCategoryId).unsafeRunSync()

        assert(expected == actual)
      }
    }

    "subscribeSupplier" - {
      "subscribeSupplier: valid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val validUserId     = "1"
        val validSupplierId = "1"
        val expected        = Right(1)

        (userDAO.subscribeSupplier _).expects(*, *).returning(1.pure[IO]).once()

        val actual = userService.subscribeSupplier(validUserId, validSupplierId).unsafeRunSync()

        assert(expected == actual)
      }

      "subscribeSupplier: invalid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val validUserId       = "1"
        val invalidSupplierId = "a"
        val expected          = Left(SupplierIdFormat)

        (userDAO.subscribeSupplier _).expects(*, *).returning(1.pure[IO]).never()

        val actual = userService.subscribeSupplier(validUserId, invalidSupplierId).unsafeRunSync()

        assert(expected == actual)
      }
    }

    "logIn" - {
      "logIn: valid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val validAuthDto = AuthDto("Arty", "1234")
        val expected     = Right("logged in") //fix later

        (userDAO.getUser _)
          .expects(*, *)
          .returning(Some(User("login", "pass", Role.Client, "em5@gmail.com")).pure[IO])
          .once()

        val actual = userService.logIn(validAuthDto).unsafeRunSync()

        assert(expected == actual)
      }

      "logIn: invalid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val invalidAuthDto = AuthDto("Arty", "1234")
        val expected       = Left(UserNotFound()) //fix later

        (userDAO.getUser _)
          .expects(*, *)
          .returning(None.pure[IO]) //fix later
          .once()

        val actual = userService.logIn(invalidAuthDto).unsafeRunSync()

        assert(expected == actual)
      }
    }

    "logOut" - {
      "logOut: valid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val validFlag = true
        val expected  = Right("logged out")

        val actual = userService.logOut(validFlag).unsafeRunSync()

        assert(expected == actual)
      }

      "logOut: invalid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val invalidFlag = false
        val expected    = Left(UserCantLogOut())

        val actual = userService.logOut(invalidFlag).unsafeRunSync()

        assert(expected == actual)
      }
    }

  }

}
