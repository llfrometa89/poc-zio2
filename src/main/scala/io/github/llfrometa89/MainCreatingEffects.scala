package io.github.llfrometa89

import zio._

import scala.concurrent.Future
import scala.util.Try
import java.io.IOException
import scala.io.{Codec, Source}
import java.net.ServerSocket
import zio.UIO

//https://zio.dev/overview/overview_creating_effects/
object MainCreatingEffects {

  //1. From Values
  val s1 = ZIO.succeed(42)

  //2' From Failure Values
  val f1 = ZIO.fail("Uh oh!")
  val f2 = ZIO.fail(new Exception("Uh oh!"))
  //3. From Scala Values
  // 3.1- Option
  val zoption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))
  val zoption2: ZIO[Any, String, Int] = zoption.orElseFail("It wasn't there!")

  case class User(id: String, teamId: String)
  case class Team(id: String)
  val maybeId: ZIO[Any, Option[Nothing], String] =
    ZIO.fromOption(Some("abc123"))
  def getUser(userId: String): ZIO[Any, Throwable, Option[User]] = ???
  def getTeam(teamId: String): ZIO[Any, Throwable, Team] = ???

  val result: ZIO[Any, Throwable, Option[(User, Team)]] = (for {
    id <- maybeId
    user <- getUser(id).some
    team <- getTeam(user.teamId).asSomeError
  } yield (user, team)).unsome

  // 3.2- Either
  val zeither: ZIO[Any, Nothing, String] = ZIO.fromEither(Right("Success!"))

  // 3.3- Try
  val ztry = ZIO.fromTry(Try(42 / 0))

  // 3.4- Future
  lazy val future = Future.successful("Hello!")

  val zfuture: ZIO[Any, Throwable, String] =
    ZIO.fromFuture { implicit ec =>
      future.map(_ => "Goodbye!")
    }

  //4. From Code
  //4.1- Synchronous Code
  import scala.io.StdIn

  val readLine: ZIO[Any, Throwable, String] = ZIO.attempt(StdIn.readLine())

  def printLine(line: String): UIO[Unit] = ZIO.succeed(println(line))

  val readLine2: ZIO[Any, IOException, String] =
    ZIO.attempt(StdIn.readLine()).refineToOrDie[IOException]

  //4.2- Asynchronous Code
  case class AuthError(msg: String)

  object legacy {
    def login(onSuccess: User => Unit, onFailure: AuthError => Unit): Unit = ???
  }

  val login: ZIO[Any, AuthError, User] =
    ZIO.async[Any, AuthError, User] { callback =>
      legacy.login(
        user => callback(ZIO.succeed(user)),
        err => callback(ZIO.fail(err))
      )
    }

  //4.3- Blocking Synchronous Code

  def download(url: String) =
    ZIO.attempt { Source.fromURL(url)(Codec.UTF8).mkString }

  def safeDownload(url: String) = ZIO.blocking(download(url))

  //Convert blocking code directly into a ZIO effect,
  val sleeping = ZIO.attemptBlocking(Thread.sleep(Long.MaxValue))

  //Some synchronous code can only be cancelled
  def accept(l: ServerSocket) =
    ZIO.attemptBlockingCancelable(l.accept())(ZIO.succeed(l.close()))
}
