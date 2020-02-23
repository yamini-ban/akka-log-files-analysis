package com.knoldus

import java.io.File

import akka.actor.{Actor, ActorLogging}

import scala.io.Source
import scala.language.postfixOps

class CountTag extends Actor with ActorLogging{

  def count(file: File, tag: (String, String, String)): CountOfTags = {
    if (file.isFile) {
      val source = Source.fromFile(file)
      val content = source.getLines
      val occurrenceOfTagInFile = content.foldLeft((0,0,0))((countOfTag, lineReadFromFile) => {
        if (lineReadFromFile.contains(tag._1)) (countOfTag._1 + 1, countOfTag._2, countOfTag._3)
        else if (lineReadFromFile.contains(tag._2)) (countOfTag._1, countOfTag._2 + 1, countOfTag._3)
        else if (lineReadFromFile.contains(tag._3)) (countOfTag._1, countOfTag._2, countOfTag._3 + 1)
        else countOfTag
      })
      CountOfTags(file.getPath,occurrenceOfTagInFile._1, occurrenceOfTagInFile._2, occurrenceOfTagInFile._3)
    }
    else {
      CountOfTags("", 0, 0, 0)
    }
  }

  def countTagsInAllFilesInADirectory(dirPath: String, tag1: String = "error:",
                                      tag2: String = "warn:", tag3: String = "info:"): List[CountOfTags] = {
    val directory = new File(dirPath)
    if (directory.isDirectory && directory.exists) {
      val content = directory.listFiles.toList
      content.map{
        file => count(file, (tag1, tag2, tag3))
      }
    }
    else {
      throw new CustomException("Directory does not exist.")
    }
  }

  def countAverageTagsPerFileInADirectory(dirPath: String, tag1: String = "error:",
                                          tag2: String = "warn:", tag3:String = "info:"): (Int, Int, Int) = {
    val listOfCountOfTagPerFile = countTagsInAllFilesInADirectory(dirPath, tag1, tag2, tag3)
    val lengthOfList = listOfCountOfTagPerFile.length
    val total = listOfCountOfTagPerFile.foldLeft((0, 0, 0))((result, countPerFile) => {
      val totalErrors = result._1 + countPerFile.countOfErrors
      val totalWarnings = result._2 + countPerFile.countOfWarnings
      val totalInfo = result._3 + countPerFile.countOfInfo
      (totalErrors, totalWarnings, totalInfo)
    })
    (total._1 / lengthOfList, total._2 / lengthOfList, total._3 / lengthOfList)
  }

  override def receive = {
    case "hello" => {
      val result = countAverageTagsPerFileInADirectory("logs", "error", "warn", "info")
//      sender() ! result
      println(result)
    }
    case msg => println("xhs")
  }
}
