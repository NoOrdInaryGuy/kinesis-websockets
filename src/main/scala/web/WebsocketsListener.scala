package web

import scala.concurrent.Future

abstract class WebsocketsListener[BindingType] {
  def bind(): Future[BindingType]
}
