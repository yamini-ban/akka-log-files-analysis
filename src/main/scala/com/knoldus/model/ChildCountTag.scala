package com.knoldus.model

import java.io.File

import akka.actor.{Actor, ActorLogging}
import com.knoldus.utilities.Count

class ChildCountTag extends Actor with ActorLogging{
  override def receive: Receive = {
    case file: File =>
      log.info(self.path.toString)
      sender ! Count.count(file, ("error", "warn", "info"))
    case _ => log.info("Default case met.")
  }
}
