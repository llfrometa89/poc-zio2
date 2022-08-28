package io.github.llfrometa89

import zio._

object MainIntegrationExample {

  val runtime = Runtime.default

  Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe
      .run(ZIO.attempt(println("Hello World!")))
      .getOrThrowFiberFailure()
  }
}
