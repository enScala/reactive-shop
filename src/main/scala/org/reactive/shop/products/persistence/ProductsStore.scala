package org.reactive.shop.products.persistence

import org.reactive.shop.products.model.Product
import org.reactive.shop.products.persistence.events.{UpdateProductEvent, InsertProductEvent, ProductsEvent}

case class ProductsStore(var products: List[Product] = Nil) extends Serializable {

  def updateState(event: ProductsEvent) = event match {
    case InsertProductEvent(product: Product) =>
      products = product :: products
    case UpdateProductEvent(product: Product) =>
      products = product :: products.filterNot(_.id == product.id)
  }

  def getProducts: List[Product] = products

  override def toString: String = products.toString
}
