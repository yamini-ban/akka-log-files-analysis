package com.knoldus.Actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit}

import org.scalatest.flatspec.AsyncFlatSpec

class ReceiverSpec extends TestKit(ActorSystem("MySpec"))
  with ImplicitSender with AnyWordSpecLike with Matchers with BeforeAndAfterAll {}
