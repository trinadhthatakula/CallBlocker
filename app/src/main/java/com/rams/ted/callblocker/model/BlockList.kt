package com.rams.ted.callblocker.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class BlockList(var numbers: List<String> = emptyList())

fun BlockList.getAsString(): String = Json.encodeToString(this)

fun String.toBlockList(): BlockList = Json.decodeFromString(this)
