package ru.sber.cb.ap

import akka.actor.{Actor, ActorLogging, Props}

package object domain {
  type CategoryName = String

  class Category(val name: CategoryName) extends Actor with ActorLogging {
    override def preStart(): Unit = log.info("Category {} Created", name)

    override def postStop(): Unit = log.info("Category {} Created", name)

    override def receive = Actor.emptyBehavior
  }

  object Category {
    def apply = props _

    def props(name: CategoryName): Props = Props(new Category(name))
  }

}
