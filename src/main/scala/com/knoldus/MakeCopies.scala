package com.knoldus

import java.io.File

import org.apache.commons.io.FileUtils

object MakeCopies {
  def make(fileName: String, numberOfCopies: Int = CustomConfig.numberOfLogFilesToBeCreated, dirName: String = "logs"): List[String] = {
    val file = new File(fileName)
    if (file.exists && file.isFile) {
      new File(dirName).mkdir
      val newFileNames = for (i <- 1 to numberOfCopies) yield {
        val tempFile = new File(s"$dirName/${file.getName}$i")
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
