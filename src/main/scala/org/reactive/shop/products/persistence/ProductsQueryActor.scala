package org.reactive.shop.products.persistence

import akka.actor.{Actor, ActorLogging}
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import org.reactive.shop.products.persistence.ProductsCommandActor.ProductsEvent
import org.reactive.shop.products.persistence.ProductsQueryActor.ProductsQuery

class ProductsQueryActor(implicit val materializer: ActorMaterializer) extends Actor with ActorLogging {

  private var productsStore: ProductsStore = new ProductsStore

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    val queries = PersistenceQuery(context.system).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
    val src = queries.eventsByPersistenceId(ProductsCommandActor.PERSISTENCE_ID)
    val events = src.map[ProductsEvent](_.event.asInstanceOf[ProductsEvent])
    events.runForeach(productsStore.updateState)
  }

  override def receive: Actor.Receive = {
    case ProductsQuery =>
      sender() ! productsStore.getProducts
  }
}

object ProductsQueryActor {
  case object ProductsQuery
}