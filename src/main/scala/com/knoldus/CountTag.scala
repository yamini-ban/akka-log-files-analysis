package com.knoldus

import java.io.File

import akka.actor.{Actor, ActorLogging}
import com.knoldus.MakeCopies.make
import com.knoldus.model.{CountOfTags, CustomException, DirName, TagsCountInAFile}

import scala.io.Source
import scala.language.postfixOps

class CountTag extends Actor with ActorLogging {

  override def preStart: Unit = {
    make("src/main/scala/com/knoldus/browser.log")
  }

  override def receive: Receive = {
    case file: File => //log.info(CountTags.count(file,("error", "warn", "info")).toString)
      sender ! count(file, ("error", "warn", "info"))
    case dirname: DirName => val files = listAllFiles(dirname.path)
      sender ! files.map(file => file.getName)
    case countTagsForAllFiles: TagsCountInAFile =>
      val listOfFiles = listAllFiles(countTagsForAllFiles.dirname)
      sender ! listOfFiles.map(file => count(file, ("error", "warn", "info")))

    case (dir:String, tag:String) => sender ! countAverageTagPerFileInADirectory(dir, tag)
    case _ => log.info("default case met.")
      sender ! "default case met"
  }

  /**
   * This method count the frequency of all the three tags given as a tuple
   *
   * @param file which is to be read for the frequency of tags
   * @param tag  which is to be searched
   * @return object of case class CountOfTags
   */
  def count(file: File, tag: (String, String, String)): CountOfTags = {
    if (file.isFile) {
      val source = Source.fromFile(file)
      val content = source.getLines
      val occurrenceOfTagInFile = content.foldLeft((0, 0, 0))((countOfTag, lineReadFromFile) => {
        if (lineReadFromFile.contains(tag._1)) (countOfTag._1 + 1, countOfTag._2, countOfTag._3)
        else if (lineReadFromFile.contains(tag._2)) (countOfTag._1, countOfTag._2 + 1, countOfTag._3)
        else if (lineReadFromFile.contains(tag._3)) (countOfTag._1, countOfTag._2, countOfTag._3 + 1)
        else countOfTag
      })
      CountOfTags(file.getPath, occurrenceOfTagInFile._1, occurrenceOfTagInFile._2, occurrenceOfTagInFile._3)
    }
    else {
      CountOfTags("", 0, 0, 0)
    }
  }

  def listAllFiles(dirPath: String): List[File] = {
    val directory = new File(dirPath)
    directory.listFiles.filter(file => file.isFile).toList
  }

  def countAverageTagPerFileInADirectory(dirPath: String, tag1: String = "error:"): Int = {
    val listOfCountOfTagPerFile = countTagsInAllFilesInADirectory(dirPath, tag1, "warn", "info")
    val lengthOfList = listOfCountOfTagPerFile.length
    val total = listOfCountOfTagPerFile.foldLeft(0)((result, countPerFile) => {
      val totalErrors = result + countPerFile.countOfErrors
      totalErrors
    })
    total / lengthOfList
  }

  def countTagsInAllFilesInADirectory(dirPath: String, tag1: String = "error:",
                                      tag2: String = "warn:", tag3: String = "info:"): List[CountOfTags] = {
    val directory = new File(dirPath)
    if (directory.isDirectory && directory.exists) {
      val content = directory.listFiles.filter(file => file.isFile).toList
      content.map {
        file => count(file, (tag1, tag2, tag3))
      }
    }
    else {
      throw new CustomException("Directory does not exist.")
    }
  }
}
