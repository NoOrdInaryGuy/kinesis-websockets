import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import client.KinesisClientFactory
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

object BootDebugReader extends App {
  val host = "127.0.0.1"
  val port = 8095
  val kinesisEndpoint = "https://kinesis.eu-west-1.amazonaws.com"

  implicit val system = ActorSystem("akka-kinesis-system")
  implicit val fm = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val credentialsProvider = new DefaultAWSCredentialsProviderChain()
  val clientFactory = new KinesisClientFactory(credentialsProvider, kinesisEndpoint)

  val streamName = "dev-spike-stream"
  val shardId = "shardId-000000000000"

  val kinesisClient = clientFactory.instance

  val shardIteratorFuture = kinesisClient.getShardIterator(streamName, shardId)

  shardIteratorFuture.foreach { shardIterator =>
    println(shardIterator)
    while(true) {
      kinesisClient.getRecords(shardIterator).foreach { records =>
        println(s"Record Count: ${records.size}")
        records.foreach { record =>
          val recordString = new String(record.getData.array())
          println(s"\tRecord Sequence: ${record.getSequenceNumber}")
          println(s"\t\tPartition: ${record.getPartitionKey}")
          println(s"\t\tText: $recordString")
          print("\n\n\n")
        }
      }
      Thread.sleep(5000)
    }
  }

}