package ru.sber.cb.ap

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import akka.util.Timeout
import ru.sber.cb.ap.domain.actor.GroupingActor

import scala.collection.immutable.HashMap
import scala.concurrent.duration._


package object domain {
  type CategoryName = String
  type CategoryRegistry = HashMap[CategoryName, ActorRef]

  implicit val timeout: Timeout = Timeout(5 seconds)
  val emptyCategoryRegistry = HashMap.empty[CategoryName, ActorRef]

  class Category(val name: CategoryName) extends Actor with Stash with ActorLogging {

    import Category._

    private var registry: CategoryRegistry = emptyCategoryRegistry

    override def preStart(): Unit = log.info("Category {} Created", name)

    override def postStop(): Unit = log.info("Category {} Created", name)

    override def receive: Receive = listen

    private def listen: Receive = {
      case GetSubcategories(replayTo) =>
        log.info("GetSubcategories replayTo={}", replayTo)
        if (registry.isEmpty)
          replayTo ! Nil
        else {
          val collector = context.actorOf(GroupingActor(registry.size, self))
          context.watch(collector)
          registry.values.foreach(_ ! GetSubcategories(collector))
          unstashAll()
          context.become(waitBySubcategoriesAggregate(replayTo))
        }
      case GetSubcategories =>
        self ! GetSubcategories(sender())
      case AddSubcategory(categoryName) =>
        sender() ! addSubcategory(categoryName)
      //      case x =>
      //        log.error("{}",x)
      //        throw new RuntimeException(s"WTF:$x")
      case _ => stash()
    }


    private def waitBySubcategoriesAggregate(replayTo: ActorRef): Receive = {
      case listOfList: List[List[ActorRef]] =>
        replayTo ! (List(registry.values.toSeq.reverse) ::: listOfList).flatten
        unstashAll()
        context.become(listen)
      //case x => throw new RuntimeException(s"WTF:$x")
      case _ => stash()
    }

    private def addSubcategory(subcategory: CategoryName): ActorRef = {
      if (!registry.contains(subcategory)) {
        val sub = context.actorOf(Category(subcategory), subcategory)
        context.watch(sub)
        registry = registry + (subcategory -> sub)
      }

      registry(subcategory)
    }

  }

  object Category {
    val rootCategoryName = "category"

    def apply(name: CategoryName): Props = Props(new Category(name))

    final case class AddSubcategory(name: CategoryName)

    final case class GetSubcategories(replayTo: ActorRef)

    final case object GetSubcategories

  }

}
