package com.knoldus

import java.time.Instant

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
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
  val result = actor ? ("logs", "error")

  val start = Instant.now()

  val listResult = result.mapTo[Double].recover({
    case _: Exception => -1.toDouble
  })

  Thread.sleep(ActorConfig.durationForActorInMain)
  logger.info("Main Result ::::::: " + listResult)
  val end = Instant.now
  logger.info("duration: " + (end.getEpochSecond - start.getEpochSecond))

}
