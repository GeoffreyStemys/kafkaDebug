package com.example.demo

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true, ignoreInvalidFields = false)
data class AppProperties(
  val kafka: Kafka,
) {

  data class Kafka(
    val partitions: Int,
    val replication: Short,
    val fetchMinBytesConfig: Int,
    val fetchMaxWaitMsConfig: Int,
  )


}
