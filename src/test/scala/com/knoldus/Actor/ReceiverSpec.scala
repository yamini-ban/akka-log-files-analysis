package com.knoldus.Actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit}

class ReceiverSpec extends TestKit(ActorSystem("MySpec"))
  with ImplicitSender with AnyWordSpecLike with Matchers with BeforeAndAfterAll {

    override def afterAll: Unit = {
      TestKit.shutdownActorSystem(system)
    }

    "An Echo actor" must {

      "send back messages unchanged" in {
        val echo = system.actorOf(TestActors.echoActorProps)
        echo ! "hello world"
        expectMsg("hello world")
      }

    }
  }
}
