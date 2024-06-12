package com.example.demo

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/demo"])
class Controller(
  private val kafkaTemplate: KafkaTemplate<String, DemoEvent>
) {

  val logger = KotlinLogging.logger {}

  @PostMapping("/kafka")
  fun addEvent(@RequestParam("event") event: String): ResponseEntity<Void> {
    repeat(10000) { number ->
      KafkaTopics.getValues().forEach { topic ->
        repeat(2) { partition ->
          val demoEvent = DemoEvent(partition = partition, value = number)
          kafkaTemplate.send(topic, partition, demoEvent.hashCode().toString(), demoEvent)
        }
      }
    }
    return ResponseEntity.ok().build()
  }

}
