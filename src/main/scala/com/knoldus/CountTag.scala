package com.knoldus

import java.io.File

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.knoldus.model._
import com.knoldus.utilities.Count
import com.knoldus.utilities.MakeCopies.make

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class CountTag extends Actor with ActorLogging {

  override def preStart: Unit = {
    make("src/main/scala/com/knoldus/browser.log")
  }

  override def receive: Receive = {
    case file: File =>
            log.info(self.path.toString)
            sender ! Count.count(file, ("error", "warn", "info"))
    case dirname: DirName =>
            log.info(self.path.toString)
            val files = listAllFiles(dirname.path)
            sender ! files.map(file => file.getName)
    case countTagsForAllFiles: TagsCountInAFile =>
            log.info(self.path.toString)
            val result = countTagsInAllFilesInADirectory(
              countTagsForAllFiles.dirname,
              countTagsForAllFiles.tag1,
              countTagsForAllFiles.tag2,
              countTagsForAllFiles.tag3
            )
             log.info(result.map(list => sender ! list).toString)
    case (dir:String, tag:String) =>
            log.info(context.dispatcher.toString)
            log.info("Control is here......................................................")
            log.info("Thread......." + Thread.currentThread().getName)
            val result = countAverageTagPerFileInADirectory(dir, tag)
            log.info(result.map(res => {
              sender ! res
              res
            }).toString)
    case _ => log.info("default case met.")
            sender ! "default case met"
  }

  /**
   * This method list all the file in a directory
   * @param dirPath to fetch all the files
   * @return list of all the files
   */
  def listAllFiles(dirPath: String): List[File] = {
    val directory = new File(dirPath)
    directory.listFiles.filter(file => file.isFile).toList
  }

  /**
   * This method counts average frequency of given tag in a file
   * @param dirPath path of directory in which file lies
   * @param tag to search for frequency
   * @return average count
   */
  def countAverageTagPerFileInADirectory(dirPath: String, tag: String = "error:"): Future[Double] = {
    val listOfCountOfTagPerFile = countTagsInAllFilesInADirectory(dirPath, tag, "warn", "info")
    listOfCountOfTagPerFile.recover{
      case _:Exception => -1
    }
    for (list <- listOfCountOfTagPerFile) yield {
      val lengthOfList = list.length
      val total = list.foldLeft(0)((result, countPerFile) => {
        val totalErrors = result + countPerFile.countOfErrors
        totalErrors
      })
      log.info((total / lengthOfList).toString)
      (total / lengthOfList).toDouble
    }
  }

  /**
   * Counts tags frequency in all the file in a directory.
   * @param dirPath directory path.
   * @param tag1 to search for.
   * @param tag2 to search for.
   * @param tag3 to search for.
   * @return list of case class CountOfTags which holds file name and count of each tag.
   */
  def countTagsInAllFilesInADirectory(dirPath: String, tag1: String = "error:",
                                      tag2: String = "warn:", tag3: String = "info:"): Future[List[CountOfTags]] = {
    val directory = new File(dirPath)
    if (directory.isDirectory && directory.exists) {
      val router = context.actorOf(RoundRobinPool(3).props(Props[ChildCountTag].withDispatcher("child-count-tag-dispatcher")))
      val content = directory.listFiles.filter(file => file.isFile).toList
      Future.sequence(content.map {
        file => implicit val timeout = Timeout(3 second)
          val result = router ? file
          val count = result.mapTo[CountOfTags].recover{
            case exception: Exception => CountOfTags("", 0, 0, 0)
          }
          Thread.sleep(1*1000)
          count
      })
    }
    else {
      throw new CustomException("Directory does not exist.")
    }
  }
}
