# kinesis-websockets

* Proof of concept code that allows clients to push data onto a Kinesis stream via a WebSocket.
* WebSockets support is from the new Akka-HTTP WebSockets API (currently using Akka-HTTP-RC3), and this repository
  provides an example of using this API in a real system.
* The path to which the data is pushed is used as the shard key for consistent processing.
