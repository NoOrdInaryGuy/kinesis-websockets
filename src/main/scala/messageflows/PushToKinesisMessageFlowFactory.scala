package messageflows

import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.stream.scaladsl._
import client.KinesisClient
class PushToKinesisMessageFlowFactory(kinesisClient: KinesisClient, streamName: String) extends MessageFlowFactory {

  private val websocketLogger = Sink.foreach[Message] {
    message => println(s"Message received: $message")
  }

  private def kinesisSink(partitionKey: String) = Sink.foreach[Message] {
    message => {
      message match {
        case TextMessage.Strict(text) => kinesisClient.putRecord(streamName, text, partitionKey)
        case _ => println("Currently only supporting TextMessage.String websocket messages.")
      }
    }
  }

  private val linkFlow: Flow[Message, Message, Unit] = Flow[Message].filter {
    //Passes nothing through, but lets complete/cancel propagate.
    in => false
  }

  /**
   * Returns a [[Flow]] that logs WebSocket [[Message]]s, and pushes them to Kinesis.
   * No messages are returned in the current implementation, but the link junction is
   * include so that cancel/complete callbacks can propagate.
   *
   * {{{
   *     +--------------------------------------------+
   *     | Resulting Flow                             |
   *     |                       +-----------------+  |
   *     |                       |                 |  |
   *     |                   ~~> |   kinesisSink   |  |
   *     |                  /    |                 |  |
   *     |                 /     +-----------------+  |
   *     |                /                           |
   *     |  +-----------+/       +-----------------+  |
   *     |  |           |        |                 |  |
   * In ~~> | broadcast | ~~>    | websocketLogger |  |
   *     |  |           |        |                 |  |
   *     |  +-----------+\       +-----------------+  |
   *     |                \                           |
   *     |                 \     +-----------------+  |
   *     |                  \    |                 |  |
   *     |                   ~~> |      link       | ~~> Out
   *     |                       |                 |  |
   *     |                       +-----------------+  |
   *     |                                            |
   *     +--------------------------------------------+
   * }}}
   */
  def getFlow(shardKey: String): Flow[Message, Message, Unit] = Flow() {
    //Caching?
    implicit builder =>
      import FlowGraph.Implicits._

      println(s"getFlow called for shardKey $shardKey")

      val bcast = builder.add(Broadcast[Message](3))
      val link = builder.add(linkFlow)

      bcast ~> websocketLogger
      bcast ~> kinesisSink(shardKey)
      bcast ~> link
      //Without this, downstream consumers hanging from the outlet are not told when upstream completes.

      (bcast.in, link.outlet)
  }
}
