package com.example.demo

import com.example.demo.KafkaTopics.DEBUG1
import com.example.demo.KafkaTopics.DEBUG2
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.TopicPartition
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Thread.sleep

@Service
@Transactional(readOnly = false)
class Orchestrator {

  val logger = KotlinLogging.logger {}

  @KafkaListener(
    groupId = "$DEBUG1-0", containerFactory = "kafkaListenerContainerFactory",
    topicPartitions = [TopicPartition(partitions = ["0", "1"], topic = DEBUG1)]
  )
  fun debug10(demoEvents: List<DemoEvent>) {
    val groups = demoEvents.groupBy { it.partition }.mapValues { it.value.size }
    logger.info { "listener blocking: $groups events" }
    sleep(10000)
  }

  @KafkaListener(
    groupId = "$DEBUG2-0", containerFactory = "kafkaListenerContainerFactory",
    topicPartitions = [TopicPartition(partitions = ["0"], topic = DEBUG2)]
  )
  fun debug20(demoEvents: List<DemoEvent>) {
    val groups = demoEvents.groupBy { it.partition }.mapValues { it.value.size }
    logger.info { "listener par 0: $groups events" }
    sleep(10000)
  }
  @KafkaListener(
    groupId = "$DEBUG2-1", containerFactory = "kafkaListenerContainerFactory",
    topicPartitions = [TopicPartition(partitions = ["1"], topic = DEBUG2)]
  )
  fun debug21(demoEvents: List<DemoEvent>) {
    val groups = demoEvents.groupBy { it.partition }.mapValues { it.value.size }
    logger.info { "listener par 1: $groups events" }
    sleep(10000)
  }

}
