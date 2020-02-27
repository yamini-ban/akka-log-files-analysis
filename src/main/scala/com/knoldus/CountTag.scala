package com.knoldus

import java.io.File

import com.knoldus.model.{CustomException, TotalTagsInDirectory, CountOfTags}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

object CountTag {
  /**
   * This method counts average frequency of given tag in a file
   *
   * @param dirPath path of directory in which file lies
   * @param tag1 is the tag to search for frequency
   * @return average count
   */
  def countAverageTagsPerFileInADirectory(dirPath: String = CustomConfig.logsDirectoryName, tag1: String = "error:"): Future[Double] = {
    for (total <- countTotalTagsInAllFilesInADirectory(dirPath, tag1)) yield (total.totalErrors / total.noOfFiles).toDouble
  }

  /**
   * Counts tags frequency in all the file in a directory.
   * @param dirPath directory path.
   * @param tag1    to search for.
   * @param tag2    to search for.
   * @param tag3    to search for.
   * @return list of case class CountOfTags which holds file name and count of each tag.
   */
  def countTotalTagsInAllFilesInADirectory(dirPath: String = CustomConfig.logsDirectoryName, tag1: String = "error:",
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

  /**
   * * Counts occurrence of all the three tags in a file.
   * @param file file to be read.
   * @param tag is the tuple of three tags
   * @return object of case class
   */
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
