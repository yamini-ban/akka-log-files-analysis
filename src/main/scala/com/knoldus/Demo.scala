package com.knoldus

import java.io.File

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.knoldus.model.CountOfTags

import scala.concurrent.Future
import scala.concurrent.duration._

class Demo extends Actor with ActorLogging{
  override def receive: Receive = {
    case file: File => //log.info(CountTags.count(file,("error", "warn", "info")).toString)
      sender ! (new CountTag).count(file, ("error", "warn", "info"))
  }
}

object Fail

object DemoDriver extends App {
  val system = ActorSystem("demo-ing")
//  val actor = system.actorOf(Props[Demo], "yb")
  val listOfFiles = new File("logs").listFiles.toList
  val listOfFileWithCountOFTags = Future.sequence(listOfFiles.map{
    file => {
      implicit val timeout: Timeout = 5.seconds
      val actor = system.actorOf(Props[Demo])
      val count = actor ? file
      count.mapTo[CountOfTags].recover({
        case _: Exception => CountOfTags("", 0, 0, 0)
      })
    }
  })
  Thread.sleep(2*1000)
  listOfFileWithCountOFTags.foreach(println)
}
