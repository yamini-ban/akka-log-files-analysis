package com.knoldus.utilities

import akka.actor.ActorSystem

object ActorConfig {

  val system: ActorSystem = ActorSystem("AnalyseLogs")

  val noOfChildActorInPool = 6

  val durationForChildSleep: Int = 1500

  val durationForActorInMain: Int = 10*1000

  val numberOfLogFileToBeCreated: Int = 10

  val supervisorNumberOfRetries: Int = 5

  val invalidMessage = 1

}
