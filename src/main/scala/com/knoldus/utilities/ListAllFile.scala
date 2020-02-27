package com.knoldus.utilities

import java.io.File

object ListAllFile {
  /**
   * This method list all the file in a directory
   * @param dirPath to fetch all the files
   * @return list of all the files
   */
  def listAllFiles(dirPath: String): List[File] = {
    val directory = new File(dirPath)
    if (directory.exists) {
      directory.listFiles.filter(file => file.isFile).toList
    }
    else List.empty[File]
  }
}
