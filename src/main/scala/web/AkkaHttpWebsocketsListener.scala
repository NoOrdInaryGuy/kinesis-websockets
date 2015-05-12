package web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.{Sink, Source}
import config.WSServerConfig

import scala.concurrent.{ExecutionContext, Future}

class AkkaHttpWebsocketsListener(wsConfig: WSServerConfig, route: Route)
                                (implicit system: ActorSystem, fm: FlowMaterializer, ex: ExecutionContext)
  extends WebsocketsListener[Http.ServerBinding] {

  private lazy val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http(system).bind(interface = wsConfig.host, port = wsConfig.port)

  def bind(): Future[Http.ServerBinding] = {
    serverSource.to(Sink.foreach {
      connection =>
        println("Accepted new connection from: " + connection.remoteAddress)
        connection handleWith Route.handlerFlow(route)
    }).run()
  }
}
