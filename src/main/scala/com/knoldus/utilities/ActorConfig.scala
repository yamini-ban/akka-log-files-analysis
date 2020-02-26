package com.knoldus.utilities

import akka.actor.ActorSystem

object ActorConfig {
  val system: ActorSystem = ActorSystem("AnalyseLogs")

  val noOfChildActorInPool = 3

  val durationForChildSleep: Int = 3*1000

  val durationForActorInMain: Int = 10*1000

  val numberOfLogFileToBeCreated: Int = 10

  val supervisorNumberOfRetries: Int = 10

}
