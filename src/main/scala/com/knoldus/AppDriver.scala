package com.knoldus

import java.time.Instant

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps


object AppDriver extends App {
  val system = ActorSystem("AnalyseLogs")
  val prop = Props[CountTag]
  val actor = system.actorOf(prop, "GetAverageCount.")

  implicit val timeout: Timeout = 5.seconds
  println(actor.path)
  val start = Instant.now()
  val result = actor ? ("logs", "warn")
  val intResult = result.recover({
    case _: Exception => Future.failed(new Exception("result failed"))
  })
  Thread.sleep(5000)
  println(result)
  val end = Instant.now
  println(end.getEpochSecond - start.getEpochSecond)
}
