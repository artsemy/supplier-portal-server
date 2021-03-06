package com.internship.service.impl

import cats.implicits._
import cats.effect.{IO, Sync}
import com.internship.dao.UserDAO
import com.internship.domain.{FullUser, Role}
import com.internship.dto.AuthDto
import com.internship.error.UserError._
import com.internship.constant.ConstantStrings._
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec

class UserServiceImplTest extends AnyFreeSpec with MockFactory {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  "Methods tests" - {

    "logIn" - {
      "logIn: valid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val validAuthDto = AuthDto("Arty", "1234")
        val user         = FullUser("login", "pass", Role.Client, "em5@gmail.com")
        val expected     = Right(user)

        (userDAO.getUser _)
          .expects(*, *)
          .returning(Some(user).pure[IO])
          .once()

        val actual = userService.logIn(validAuthDto).unsafeRunSync()

        assert(expected == actual)
      }

      "logIn: invalid" in {
        val userDAO     = mock[UserDAO[IO]]
        val userService = new UserServiceImpl[IO](userDAO)

        val invalidAuthDto = AuthDto("Arty", "1234")
        val expected       = Left(UserNotFound())

        (userDAO.getUser _)
          .expects(*, *)
          .returning(None.pure[IO])
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
        val expected  = Right(LogOutMessage)

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
