package client

import com.amazonaws.services.kinesis.AmazonKinesisAsync
import com.amazonaws.services.kinesis.model.ShardIteratorType._
import com.amazonaws.services.kinesis.model._

import scala.collection.immutable.{IndexedSeq, Seq}
import scala.concurrent.Future

trait KinesisClient {
  def underlying: AmazonKinesisAsync

  def listStreams(): Future[ListStreamsResult]

  def listStreams(limit: Option[Int]): Future[ListStreamsResult]

  def getShards(streamName: String): Future[Seq[Shard]]

  def putRecord(streamName: String, data: String, partitionKey: String): Future[PutRecordResult]

  def getRecords(shardIterator: String): Future[IndexedSeq[Record]]

  def getShardIterator(streamName: String, shardId: String, iteratorType: ShardIteratorType = TRIM_HORIZON): Future[String]
}
