package client

import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClient

class KinesisClientFactory(
                            credentialsProvider: AWSCredentialsProviderChain = new DefaultAWSCredentialsProviderChain(),
                            endpoint: String
                            ) {

  lazy val instance = {
    val client: AmazonKinesisAsyncClient = new AmazonKinesisAsyncClient(credentialsProvider).withEndpoint(endpoint)
    new WrappedAmazonKinesisAsyncClient(client)
  }

}
