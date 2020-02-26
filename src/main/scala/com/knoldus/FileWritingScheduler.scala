package com.knoldus

import akka.actor.{Cancellable, Props}
import com.knoldus.Actor.Receiver
import com.knoldus.model.AverageCount
import com.knoldus.utilities.ActorConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class FileWritingScheduler {
  def run: Cancellable = {
    val fileWriter = ActorConfig.system.actorOf(Props[Receiver], "file-writer")
    ActorConfig.system.scheduler.scheduleWithFixedDelay(0.milliseconds, 5.minutes, fileWriter, AverageCount("logs", "error"))
  }
}
