package com.knoldus

import com.knoldus.CountTag.{countAverageTagsPerFileInADirectory, countTagsInAllFilesInADirectory}
import com.knoldus.MakeCopies.make

object AppDriver extends App {

  val a = make("src/main/scala/com/knoldus/browser.log")
    val b = countTagsInAllFilesInADirectory("logs", "error", "warn", "info")
      val c = countAverageTagsPerFileInADirectory("logs", "error", "warn", "info")
        Thread.sleep(5*1000)
  println(a)

  println(b)

  println(c)

}
