package config

case class KinesisWebsocketsConfig(wsServer: WSServerConfig,
                                   kinesis: KinesisConfig)

case class WSServerConfig(host: String,
                          port: Int,
                          actorSystem: String)

case class KinesisConfig(streamName: String,
                         endpoint: String)