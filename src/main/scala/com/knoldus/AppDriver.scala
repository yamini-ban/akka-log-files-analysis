package com.knoldus

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import scala.language.postfixOps

//import com.knoldus.CountTag.{countAverageTagsPerFileInADirectory, countTagsInAllFilesInADirectory}

object AppDriver extends App {
//  val a = make("src/main/scala/com/knoldus/browser.log")
//  val b = countTagsInAllFilesInADirectory("logs", "error", "warn", "info")
//  val c = countAverageTagsPerFileInADirectory("logs", "error", "warn", "info")
  val system = ActorSystem("AnalyseLogs")
  val prop = Props[CountTag]
  val actor = system.actorOf(prop)

  implicit val timeout: Timeout = Timeout(5 second)
  println(actor.path)
  val result = actor ? "_averageCount"
//  actor ! "_averageCount"
    println(result.mapTo[Int])
  println(actor)
  ////  system.stop(actor)
}
