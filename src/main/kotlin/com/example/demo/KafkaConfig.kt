package com.example.demo

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer


@Configuration
@EnableKafka
class KafkaConfig(
  appProperties: AppProperties,
  private val beanFactory: ConfigurableListableBeanFactory
) {

  val logger = KotlinLogging.logger {}

  private val kafka: AppProperties.Kafka = appProperties.kafka

  @Value(value = "\${spring.kafka.bootstrap-servers}")
  lateinit var kafkaHost: String

  init {
    beanKafkaTopics()
  }

  private fun beanKafkaTopics() {
    KafkaTopics.getValues()
      .map { NewTopic(it, kafka.partitions, kafka.replication) }
      .forEach { topic -> beanFactory.registerSingleton(topic.name(), topic) }
  }

  @Bean
  fun objectMapper(): ObjectMapper = jacksonObjectMapper().let {
    it.disable(WRITE_DATES_AS_TIMESTAMPS) // Disable writing dates as timestamps to align with Spring Boot's default
    it.disable(FAIL_ON_UNKNOWN_PROPERTIES) // Disable failing on unknown properties to align with Spring Boot's default
    it.setSerializationInclusion(NON_NULL) // Include non-null values, which is a common setting but not strictly a Spring Boot default
    it.findAndRegisterModules() // Find and register all modules on the classpath (this is a good practice)
  }

  @Bean
  fun producerFactory(objectMapper: ObjectMapper): ProducerFactory<String, DemoEvent> =
    DefaultKafkaProducerFactory<String, DemoEvent>(mapOf(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaHost
    )).apply {
      keySerializer = StringSerializer()
      valueSerializer = JsonSerializer<DemoEvent>(objectMapper)
    }

  @Bean
  fun consumerFactory(objectMapper: ObjectMapper): ConsumerFactory<String, DemoEvent> =
    DefaultKafkaConsumerFactory<String, DemoEvent>(
      mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaHost,
        ConsumerConfig.FETCH_MIN_BYTES_CONFIG to kafka.fetchMinBytesConfig,
        ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG to kafka.fetchMaxWaitMsConfig
      ))
      .apply {
        setKeyDeserializer(StringDeserializer())
        val deserializer = JsonDeserializer<DemoEvent>(objectMapper).apply { addTrustedPackages("*", "com.example.demo.DemoEvent") }
        setValueDeserializer(deserializer)
      }


  @Bean
  fun kafkaTemplate(producer: ProducerFactory<String, DemoEvent>) = KafkaTemplate(producer)

  @Bean
  fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, DemoEvent>): ConcurrentKafkaListenerContainerFactory<String, DemoEvent> {
    val factory = ConcurrentKafkaListenerContainerFactory<String, DemoEvent>()
    factory.consumerFactory = consumerFactory
    factory.isBatchListener = true
    factory.setConcurrency(kafka.partitions)
    return factory
  }

}
