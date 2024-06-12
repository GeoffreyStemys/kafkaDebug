package com.example.demo

import kotlin.reflect.full.memberProperties

object KafkaTopics {

  const val DEBUG1 = "debug1"
  const val DEBUG2 = "debug2"

  fun getKeys() = KafkaTopics::class.memberProperties.map { it.name }.toSet()
  fun getValues() = KafkaTopics::class.memberProperties.map { it.getter.call().toString() }.toSet()

}
