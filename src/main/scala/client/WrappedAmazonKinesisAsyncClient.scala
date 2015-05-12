package client

import java.nio.ByteBuffer

import client.util.PromiseAsyncHandler
import PromiseAsyncHandler._
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClient
import com.amazonaws.services.kinesis.model.ShardIteratorType._
import com.amazonaws.services.kinesis.model._

import scala.collection.JavaConversions._
import scala.collection.immutable._
import scala.concurrent.Future

class WrappedAmazonKinesisAsyncClient(val underlying: AmazonKinesisAsyncClient) extends KinesisClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  def listStreams(): Future[ListStreamsResult] = {
    listStreams(None)
  }

  def listStreams(limit: Option[Int]): Future[ListStreamsResult] = {
    asyncToScalaFuture {
      val listStreamsRequest = new ListStreamsRequest()
      limit foreach {
        listStreamsRequest.setLimit(_)
      }
      underlying.listStreamsAsync(listStreamsRequest, _: AsyncHandler[ListStreamsRequest, ListStreamsResult])
    }
  }

  def getShards(streamName: String): Future[IndexedSeq[Shard]] = {
    asyncToScalaFuture {
      val describeStreamRequest = new DescribeStreamRequest().withStreamName(streamName)
      underlying.describeStreamAsync(describeStreamRequest, _: AsyncHandler[DescribeStreamRequest, DescribeStreamResult])
    } map {
      streamDescription => streamDescription.getStreamDescription.getShards.toVector
    }
  }

  def putRecord(streamName: String, data: String, partitionKey: String): Future[PutRecordResult] = {
    asyncToScalaFuture {
      val putRecordRequest = new PutRecordRequest()
        .withStreamName(streamName)
        .withPartitionKey(partitionKey)
        .withData(ByteBuffer.wrap(data.getBytes))
      underlying.putRecordAsync(putRecordRequest, _: AsyncHandler[PutRecordRequest, PutRecordResult])
    }
  }

  def getShardIterator(streamName: String, shardId: String, iteratorType: ShardIteratorType = TRIM_HORIZON): Future[String] = {
    asyncToScalaFuture {
      val shardIteratorRequest = new GetShardIteratorRequest()
        .withStreamName(streamName)
        .withShardId(shardId)
        .withShardIteratorType(iteratorType)
      underlying.getShardIteratorAsync(shardIteratorRequest, _: AsyncHandler[GetShardIteratorRequest, GetShardIteratorResult])
    } map {
      shardIteratorResult => shardIteratorResult.getShardIterator
    }
  }

  def getRecords(shardIterator: String): Future[IndexedSeq[Record]] = {
    asyncToScalaFuture {
      val getRecordsRequest = new GetRecordsRequest()
        .withShardIterator(shardIterator)
      underlying.getRecordsAsync(getRecordsRequest, _: AsyncHandler[GetRecordsRequest, GetRecordsResult])
    } map {
      getRecordsResult => getRecordsResult.getRecords.toVector
    }
  }

}
