package com.knoldus

import java.io.File

import com.knoldus.model.{CustomException, TotalTagsInDirectory, CountOfTags}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

object CountTag {

  def countAverageTagsPerFileInADirectory(dirPath: String, tag1: String = "error:"): Future[Double] = {
    for (total <- countTotalTagsInAllFilesInADirectory(dirPath, tag1)) yield (total.totalErrors / total.noOfFiles).toDouble
  }

  def countTotalTagsInAllFilesInADirectory(dirPath: String, tag1: String = "error:",
                                           tag2: String = "warn:", tag3: String = "info:"): Future[TotalTagsInDirectory] = Future {
    val directory = new File(dirPath)
    if (directory.isDirectory && directory.exists) {
      val content = directory.listFiles.toList
      content.map(file => count(file, (tag1, tag2, tag3))).foldLeft(TotalTagsInDirectory(0, 0, 0, 0)) {
        (result, file) =>
          TotalTagsInDirectory(
            result.noOfFiles + 1,
            result.totalErrors + file.countOfErrors,
            result.totalWarn + file.countOfWarnings,
            result.totalInfo + file.countOfInfo
          )
      }
    }
    else {
      throw new CustomException("Directory does not exist.")
    }
  }

  def count(file: File, tag: (String, String, String)): CountOfTags = {
    if (file.isFile) {
      val source = Source.fromFile(file)
      val content = source.getLines
      val occurrenceOfTagInFile = content.foldLeft((0, 0, 0))((countOfTag, lineReadFromFile) => {
        if (lineReadFromFile.contains(tag._1)) {
          (countOfTag._1 + 1, countOfTag._2, countOfTag._3)
        }
        else if (lineReadFromFile.contains(tag._2)) {
          (countOfTag._1, countOfTag._2 + 1, countOfTag._3)
        }
        else if (lineReadFromFile.contains(tag._3)) {
          (countOfTag._1, countOfTag._2, countOfTag._3 + 1)
        }
        else {
          countOfTag
        }
      })
      CountOfTags(file.getPath, occurrenceOfTagInFile._1, occurrenceOfTagInFile._2, occurrenceOfTagInFile._3)
    }
    else {
      CountOfTags("", 0, 0, 0)
    }
  }

}
