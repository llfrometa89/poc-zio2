package io.github.llfrometa89

import zio._

import java.io.IOException

//https://zio.dev/overview/overview_basic_operations
object MainBasicOperations {

  // Mapping

  val succeeded: ZIO[Any, Nothing, Int] = ZIO.succeed(21).map(_ * 2)
  val failed: ZIO[Any, Exception, Unit] =
    ZIO.fail("No no!").mapError(msg => new Exception(msg))

  // Chaining
  val sequenced: ZIO[Any, IOException, Unit] =
    Console.readLine.flatMap(input => Console.printLine(s"You entered: $input"))

  // For Comprehensions
  val program: ZIO[Any, IOException, Unit] =
    for {
      _    <- Console.printLine("Hello! What is your name?")
      name <- Console.readLine
      _    <- Console.printLine(s"Hello, ${name}, welcome to ZIO!")
    } yield ()

  // Zipping
  val zipped: ZIO[Any, Nothing, (String, Int)] =
    ZIO.succeed("4").zip(ZIO.succeed(2))

  val zipRight1: ZIO[Any, IOException, String] =
    Console.printLine("What is your name?").zipRight(Console.readLine)

  val zipRight2: ZIO[Any, IOException, String] =
    Console.printLine("What is your name?") *> Console.readLine
}
