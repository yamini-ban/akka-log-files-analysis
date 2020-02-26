package com.knoldus

import java.io.File

import akka.actor.{Actor, ActorLogging}
import com.knoldus.model.CustomException
import com.knoldus.utilities.Count

class ChildCountTag extends Actor with ActorLogging{

  override def receive: Receive = {
    case file: File =>
      log.info(self.path.toString + "......." + Thread.currentThread().getName)
      sender ! Count.count(file, ("error", "warn", "info"))

    case _ =>
      log.info(self.path.toString + "....Child Default Case......." + Thread.currentThread().getName)
      throw new CustomException("****************Unhandled Case.....")
  }
}
