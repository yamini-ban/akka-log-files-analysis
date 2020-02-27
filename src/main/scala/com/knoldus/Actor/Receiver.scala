package com.knoldus.Actor

import java.io.File

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import akka.pattern.{AskTimeoutException, ask}
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.knoldus.model._
import com.knoldus.utilities.{ActorConfig, Count, ListAllFile}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class Receiver extends Actor with ActorLogging {

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(ActorConfig.supervisorNumberOfRetries, 10.second) {
      case _: CustomException => log.info("-----------Custom case exception---------: ")
        Resume
      case _: AskTimeoutException => log.info("----------Ask exception----------")
        Resume
      case e: Exception => log.info("-----------default case exception---------: " + e)
        Restart
    }

  override def receive: Receive = {
    case AverageCount(path, tag) => log.info(self.path + "****" + Thread.currentThread().getName)
                            val result = countAverageTagPerFileInADirectory(path, tag)
                            log.info(result.map(res => { sender ! res
                              log.info("(receive method)Average: " + res.toString)}).toString)
    case TagsCountMessage(path, error, warn, info) => log.info(self.path + "****" + Thread.currentThread().getName)
                            sender ! Count.count(new File(path), error, warn, info)
    case DirNameMessage(path) => log.info(self.path + "****" + Thread.currentThread().getName)
                          val files = ListAllFile.listAllFiles(path)
                          sender ! files.map(file => file.getName)
    case TotalTagCountMessage(path, error, warn, info) => log.info(self.path + "****" + Thread.currentThread().getName)
                        val listOfFilesWithCount =
                          countTagsInAllFilesInADirectory(path, error, warn, info)
                          val total = for (list <- listOfFilesWithCount) yield {
                            list.foldLeft(0.toInt, 0.toInt, 0.toInt){(result, file) =>
                              (result._1 + file.countOfErrors, result._2 + file.countOfWarnings, result._3 + file.countOfInfo)
                            }
                          }
                          log.info(total.map(total => sender ! total).toString)
    case SchedulerMessage(path, tag) => log.info(self.path + "**Scheduler is active**" + Thread.currentThread().getName)
                          val result = countAverageTagPerFileInADirectory(path, tag)
                          log.info(result.map(res => { sender ! res
                            log.info("(schedular case in receive)Average: " + res.toString)}).toString)
    case _ => log.info(self.path.toString + "....Default Case......." + Thread.currentThread().getName)
                          throw new CustomException("*Receivers default*")
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
      case _: Exception => List(CountOfTags("", 0, 0, 0))
    }
    for (list <- listOfCountOfTagPerFile) yield {
      val lengthOfList = list.length
      val total = list.foldLeft(0)((result, countPerFile) => {
        val totalErrors = result + countPerFile.countOfErrors
        totalErrors
      })
      log.info(("Result Of Average number of errors per file = " + total / lengthOfList).toString)
      (total / lengthOfList).toDouble
    }
  }

  private def countTagsInAllFilesInADirectory(dirPath: String,tag1: String = "error:", tag2: String = "warn:",
                                      tag3: String = "info:"): Future[List[CountOfTags]] = {
    val directory = new File(dirPath)
    if (directory.isDirectory && directory.exists) {
      val childDispatcher = "count-tag-dispatcher"
      val router = context.actorOf(RoundRobinPool(ActorConfig.noOfChildActorInPool).props(Props[Receiver].withDispatcher(childDispatcher)))
      val listOfFiles = ListAllFile.listAllFiles(dirPath)
      val listOfTagsCount = Future.sequence(listOfFiles.map {
        file =>
          implicit val timeout: Timeout = 3.seconds
          val result = router ? TagsCountMessage(file.getAbsolutePath, tag1, tag2, tag3)
          val count = result.mapTo[CountOfTags].recover {
            case _: Exception => CountOfTags("", 0, 0, 0)
          }
          count
      })
      Thread.sleep(ActorConfig.durationForChildSleep)
      listOfTagsCount
    }
    else {
      throw new CustomException("Directory do not exist!!!!")
    }
  }

}
