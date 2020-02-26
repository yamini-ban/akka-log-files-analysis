package com.knoldus

import java.time.Instant

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.knoldus.Actor.Receiver
import com.knoldus.model.{AverageCount, TotalTagCountMessage}
import com.knoldus.utilities.ActorConfig
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


object AppDriver extends App {

  val logger = LoggerFactory.getLogger(this.getClass)

  val scheduler = new FileWritingScheduler

  val prop = Props[Receiver]
  val actor = ActorConfig.system.actorOf(prop, "GetAverageCount")

  logger.info(ActorConfig.system.dispatcher.toString)

  scheduler.run

  implicit val timeout: Timeout = 40.seconds
  val result1 = actor ? TotalTagCountMessage("logs")
  val result2 = actor ? AverageCount("logs", "error")

  val start = Instant.now()

  val total = result1.mapTo[(Int, Int, Int)].recover({case _: Exception => (0, 0, 0)})

  val average = result2.mapTo[Double].recover({case _: Exception => -1.toDouble})

  Thread.sleep(ActorConfig.durationForActorInMain)
  logger.info("Main Result Of Total Count Of (error, warn, info) :::::::" + total)
  logger.info("Main Result Of Average number of errors per file  :::::::" + average)
  val end = Instant.now
  logger.info("duration: " + (end.getEpochSecond - start.getEpochSecond))

}
