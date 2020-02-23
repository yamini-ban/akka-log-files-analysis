package com.knoldus

import java.io.File

import org.apache.commons.io.FileUtils

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object MakeCopies {
  def make(fileName: String, numberOfCopies: Int = 10): Future[List[String]] = Future{
    val file = new File(fileName)
    if (file.exists && file.isFile) {
      new File("logs").mkdir
      val newFileNames = for (i <- 1 to numberOfCopies) yield {
        val tempFile = new File(s"logs/${file.getName}$i")
        FileUtils.copyFile(file, tempFile)
        tempFile.getAbsolutePath
      }
      newFileNames.toList
    }
    else {
      List.empty[String]
    }
  }

}
