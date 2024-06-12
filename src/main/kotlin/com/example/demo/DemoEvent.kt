package com.example.demo

import java.util.*

data class DemoEvent(
  val uuid: String = UUID.randomUUID().toString(),
  val partition: Int,
  val value: Int
)
