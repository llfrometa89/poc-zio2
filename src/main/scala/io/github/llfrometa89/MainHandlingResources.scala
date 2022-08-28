package io.github.llfrometa89

import zio._

import java.io.IOException

//https://zio.dev/overview/overview_handling_resources
object MainHandlingResources {

  // - Finalizing
  val finalizer: UIO[Unit] =
    ZIO.succeed(println("Finalizing!"))
  // finalizer: UIO[Unit] = Sync(
  //   trace = "repl.MdocSession.App.finalizer(handling_resources.md:15)",
  //   eval = <function0>
  // )

  val finalized: IO[String, Unit] =
    ZIO.fail("Failed!").ensuring(finalizer)
  // finalized: IO[String, Unit] = Dynamic(
  //   trace = "repl.MdocSession.App.finalized(handling_resources.md:19)",
  //   update = 1L,
  //   f = zio.ZIO$$$Lambda$10363/1254819854@4c29afbf
  // )

  // - Acquire Release

  def openFile(filename: String): ZIO[Any, IOException, Array[Byte]]                       = ???
  def processFile(handle: ZIO[Any, IOException, Array[Byte]]): ZIO[Any, IOException, Unit] = ???
  def closeFile(handle: ZIO[Any, IOException, Array[Byte]]): ZIO[Any, IOException, Unit]   = ???

  val handle: ZIO[Any, IOException, Array[Byte]] = openFile("primary.json")

  try
    processFile(handle)
  finally
    closeFile(handle)

//  val groupedFileData: IO[IOException, Unit] =
//    ZIO.acquireReleaseWith(openFile("data.json"))(closeFile(_)) { file =>
//      for {
//        data    <- decodeData(file)
//        grouped <- groupData(data)
//      } yield grouped
//    }

}
