package messageflows

import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.Flow

trait MessageFlowFactory {

  def getFlow(shardKey: String): Flow[Message, Message, Unit]

}
