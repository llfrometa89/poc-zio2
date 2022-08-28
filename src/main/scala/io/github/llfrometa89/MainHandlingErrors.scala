package io.github.llfrometa89

import zio._

import java.io.{FileNotFoundException, IOException}

//https://zio.dev/overview/overview_handling_errors
object MainHandlingErrors {

  // Either
  val zeither: ZIO[Any, Nothing, Either[String, Nothing]] =
    ZIO.fail("Uh oh!").either

  // Catching All Errors
  def openFile(filename: String): ZIO[Any, IOException, Array[Byte]] = ???
  val z: ZIO[Any, IOException, Array[Byte]] =
    openFile("primary.json").catchAll { error =>
      for {
        _    <- ZIO.logErrorCause("Could not open primary file", Cause.fail(error))
        file <- openFile("backup.json")
      } yield file
    }

  // Catching Some Errors
  val data: ZIO[Any, IOException, Array[Byte]] =
    openFile("primary.data").catchSome { case _: FileNotFoundException =>
      openFile("backup.data")
    }

  // Fallback

  val primaryOrBackupData: ZIO[Any, IOException, Array[Byte]] =
    openFile("primary.data").orElse(openFile("backup.data"))

  // Folding
  lazy val DefaultData: Array[Byte] = Array(0, 0)
  val primaryOrDefaultData: ZIO[Any, Nothing, Array[Byte]] =
    openFile("primary.data").fold(
      _ => DefaultData, // Failure case
      data => data      // Success case
    )

  val primaryOrSecondaryData: ZIO[Any, IOException, Array[Byte]] =
    openFile("primary.data").foldZIO(
      _ => openFile("secondary.data"), // Error handler
      data => ZIO.succeed(data)        // Success handler
    )

//  def readUrls(filename: String): ZIO[Any, Nothing, Content] = ???
//  def fetchContent(c: Content): ZIO[Any, Nothing, Content]   = ???
//
//  trait Content
//  object Content {
//    def empty: Content                     = ???
//    def NoContent(error: Nothing): Content = ???
//  }
//
//  val urls: ZIO[Any, Nothing, Content] =
//    readUrls("urls.json").foldZIO(
//      error => ZIO.succeed(Content.NoContent(error)),
//      success => fetchContent(success)
//    )

  // Retrying
  val retriedOpenFile: ZIO[Any, IOException, Array[Byte]] =
    openFile("primary.data")
      .retry(Schedule.recurs(5))

  def retryRecovery(e: IOException, s: Long): ZIO[Any, Nothing, Array[Byte]] = ZIO.succeed(DefaultData)

  val retryOpenFile: ZIO[Any, IOException, Array[Byte]] =
    openFile("primary.data")
      .retryOrElse(Schedule.recurs(5), retryRecovery)
}
