package com.knoldus

import java.io.File

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import akka.pattern.{AskTimeoutException, ask}
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.knoldus.model._
import com.knoldus.utilities.{ActorConfig, Count}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps

class Receiver extends Actor with ActorLogging {

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(ActorConfig.supervisorNumberOfRetries, 1.second) {
      case _: CustomException => log.info("-----------Custom case exception---------: ")
        Resume
      case _: AskTimeoutException => log.info("----------Ask exception----------")
        Resume
      case e: Exception => log.info("-----------default case exception---------: " + e)
        Restart
    }

    /*override def preStart: Unit = {
    make("src/main/scala/com/knoldus/browser.log")
  }*/

  override def receive: Receive = {
    case Write(path) => val file = new File(path)
      if (file.exists && file.isFile) { val source = Source.fromFile(file)
        val content = source.getLines.toList
        content.foreach(log.info)
      }
      else {
        log.info("file does not exist....!!!...")
      }
    case file: File =>
      sender ! Count.count(file, ("error", "warn", "info"))
    case dirname: DirName =>
      val files = listAllFiles(dirname.path)
      sender ! files.map(file => file.getName)
    case countTagsForAllFiles: TagsCountInAFile =>
      val result = countTagsInAllFilesInADirectory(
        countTagsForAllFiles.dirname,
        countTagsForAllFiles.tag1,
        countTagsForAllFiles.tag2,
        countTagsForAllFiles.tag3
      )
      log.info(result.map(list => sender ! list).toString)
    case (dir: String, tag: String) =>
      log.info("Thread......." + Thread.currentThread().getName)
      val result = countAverageTagPerFileInADirectory(dir, tag)
      log.info(result.map(res => {
        sender ! res
        res
      }).toString)
    case _ => log.info(self.path.toString + "....Default Case......." + Thread.currentThread().getName)
  }

  /**
   * This method list all the file in a directory
   *
   * @param dirPath to fetch all the files
   * @return list of all the files
   */
  def listAllFiles(dirPath: String): List[File] = {
    val directory = new File(dirPath)
    directory.listFiles.filter(file => file.isFile).toList
  }

  /**
   * This method counts average frequency of given tag in a file
   *
   * @param dirPath path of directory in which file lies
   * @param tag     to search for frequency
   * @return average count
   */
  def countAverageTagPerFileInADirectory(dirPath: String, tag: String = "error:"): Future[Double] = {
    val listOfCountOfTagPerFile = countTagsInAllFilesInADirectory(dirPath, tag, "warn", "info")
    listOfCountOfTagPerFile.recover {
      case _: Exception => -1
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
   *
   * @param dirPath directory path.
   * @param tag1    to search for.
   * @param tag2    to search for.
   * @param tag3    to search for.
   * @return list of case class CountOfTags which holds file name and count of each tag.
   */
  def countTagsInAllFilesInADirectory(dirPath: String, tag1: String = "error:",
                                      tag2: String = "warn:", tag3: String = "info:"): Future[List[CountOfTags]] = {
    val directory = new File(dirPath)
    if (directory.isDirectory && directory.exists) {
      val childDispatcher = "child-count-tag-dispatcher"
      val router = context.actorOf(RoundRobinPool(ActorConfig.noOfChildActorInPool).props(Props[ChildCountTag].withDispatcher(childDispatcher)))
      val content = directory.listFiles.filter(file => file.isFile).toList
      val list = Future.sequence(content.map {
        file =>
          implicit val timeout: Timeout = 3.seconds
          router ? 1 //for actor failure
          val result = router ? file
          val count = result.mapTo[CountOfTags].recover {
            case _: Exception => CountOfTags("", 0, 0, 0)
          }
          count
      })
      Thread.sleep(ActorConfig.durationForChildSleep)
      list
    }
    else {
      throw new CustomException("Directory do not exist!!!!")
    }
  }

}
