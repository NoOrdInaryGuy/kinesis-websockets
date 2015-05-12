package web.routes

import akka.http.scaladsl.server.Directives._
import directives.CustomDirectives._
import messageflows.MessageFlowFactory

class API010(wsToKinesisFlowFactory: MessageFlowFactory) {
  val route =
    pathPrefix("ws") {
      get {
        pathToShardKey { shardKey: String =>
          handleWebsocketMessages(wsToKinesisFlowFactory.getFlow(shardKey))
        }
      }
    }
}
