package com.knoldus

import akka.actor.Cancellable
import akka.actor.Props
import com.knoldus.model.Write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import com.knoldus.utilities.ActorConfig

class FileWritingScheduler {
  def run: Cancellable = {
    val fileWriter = ActorConfig.system.actorOf(Props[Receiver], "file-writer")
    ActorConfig.system.scheduler.scheduleWithFixedDelay(0.milliseconds, 5.minutes, fileWriter, Write())
  }
}
