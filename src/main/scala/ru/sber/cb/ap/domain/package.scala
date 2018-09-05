package ru.sber.cb.ap

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.collection.immutable.HashMap

package object domain {
  type CategoryName = String
  type CategoryRegistry = HashMap[CategoryName, ActorRef]
  val emptyCategoryRegistry = HashMap.empty[CategoryName, ActorRef]

  class Category(val name: CategoryName) extends Actor with ActorLogging {

    import Category._

    var registry: CategoryRegistry = emptyCategoryRegistry

    override def preStart(): Unit = log.info("Category {} Created", name)

    override def postStop(): Unit = log.info("Category {} Created", name)

    override def receive: Receive = {
      case AddSubcategory(categoryName) ⇒
        sender() ! addSubcategory(categoryName)
    }

    private def addSubcategory(subcategory: CategoryName): ActorRef = {
      if (!registry.contains(subcategory))
        registry = registry + (subcategory -> context.actorOf(Category(subcategory), subcategory))

      registry(subcategory)
    }

  }

  object Category {
    def apply(name: CategoryName): Props = Props(new Category(name))

    val rootCategoryName = "category"

    final case class AddSubcategory(name: CategoryName)

    final case object GetSubcategories

  }

}
