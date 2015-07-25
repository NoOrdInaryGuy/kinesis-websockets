import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import client.KinesisClientFactory
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.typesafe.config.{Config, ConfigFactory}
import config.{KinesisConfig, WSServerConfig}
import messageflows.{MessageFlowFactory, PushToKinesisMessageFlowFactory}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import web.AkkaHttpWebsocketsListener
import web.routes.API010

import scala.concurrent.ExecutionContext

object BootApp extends App {
  //Config
  private val config: Config = ConfigFactory.load()
  private val kinesisConfig = config.as[KinesisConfig]("kinesisWSConfig.kinesis")
  private val wsConfig = config.as[WSServerConfig]("kinesisWSConfig.wsServer")

  //Deps
  private val credentialsProvider = new DefaultAWSCredentialsProviderChain()
  private val clientFactory = new KinesisClientFactory(credentialsProvider, kinesisConfig.endpoint)
  private val kinesisClient = clientFactory.instance
  private val wsToKinesisFlowFactory: MessageFlowFactory =
    new PushToKinesisMessageFlowFactory(kinesisClient, kinesisConfig.streamName)
  private val v010Route = new API010(wsToKinesisFlowFactory).route

  //Implicits
  implicit private val system = ActorSystem(wsConfig.actorSystem)
  implicit private val fm = ActorMaterializer()
  implicit private val executionContext: ExecutionContext = system.dispatcher

  //Routing
  private val Version = PathMatcher("""([0-9]\.[0-9]\.[0-9])""".r)

  private val route: Route =
    pathPrefix("api") {
      pathPrefix(Version) {
        case "0.1.0" => v010Route
        case _ => complete(404, "Unknown Version")
      }
    }

  //Web
  private val web = new AkkaHttpWebsocketsListener(wsConfig, route)
  web.bind()

}