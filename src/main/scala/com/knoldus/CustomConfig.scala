package com.knoldus

import com.knoldus.model.TotalTagsInDirectory

object CustomConfig {
  val logsDirectoryName: String = "logs"
  val noOfFilesInDir = 10
  val totalErrors = 1740
  val totalWarn = 250
  val totalInfo = 100140
  val total = TotalTagsInDirectory(noOfFilesInDir, totalErrors, totalWarn, totalInfo)
}
