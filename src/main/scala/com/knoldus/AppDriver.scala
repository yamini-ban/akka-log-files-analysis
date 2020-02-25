package com.knoldus

import java.time.Instant

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


object AppDriver extends App {

  val logger = LoggerFactory.getLogger(this.getClass)

  val system = ActorSystem("AnalyseLogs")
  val prop = Props[CountTag]
  val actor = system.actorOf(prop, "GetAverageCount")
//  val actor = system.actorOf(RoundRobinPool(3).props(Props[CountTag].withDispatcher("count-tag-dispatcher")), "GetAverageCount.")

  logger.info(system.dispatcher.toString)

  implicit val timeout: Timeout = 30.seconds
  val result = actor ? ("logs", "error")
//  val result = actor ? TagsCountInAFile("logs")
  val start = Instant.now()
  val listResult = result.mapTo[Double].recover({
    case _: Exception => -1.toDouble
  })
  Thread.sleep(15000)
  println("Main Result ::::::: " + listResult)
  val end = Instant.now
  println("duration: " + (end.getEpochSecond - start.getEpochSecond))
}
