package com.hoffi.mpp.common.io.json

import kotlinx.serialization.json.*

fun List<*>.toJsonElement(): JsonElement {
    val list: MutableList<JsonElement> = mutableListOf()
    this.forEach {
        val value = it ?: return@forEach
        when(value) {
            is Map<*, *> -> list.add((value).toJsonElement())
            is List<*> -> list.add(value.toJsonElement())
            else -> list.add(JsonPrimitive(value.toString()))
        }
    }
    return JsonArray(list)
}

fun Map<*, *>.toJsonElement(): JsonElement {
    val map: MutableMap<String, JsonElement> = mutableMapOf()
    this.forEach {
        val key = it.key as? String ?: return@forEach
        val value = it.value ?: return@forEach
        when(value) {
            is Map<*, *> -> map[key] = (value).toJsonElement()
            is List<*> -> map[key] = value.toJsonElement()
            else -> map[key] = JsonPrimitive(value.toString())
        }
    }
    return JsonObject(map)
}

object Generic {
    fun get(map: MutableMap<String, Any>, jsonPath: String): Any {
        var path = jsonPath
        if (path.startsWith(".")) {
            path = path.substring(1)
        }
        val els = path.split(".")
        val root = map["."] as MutableMap<*, *>
        var curr: Any = root
        val soFar = StringBuilder()
        for (i in els.indices) {
            soFar.append('.').append(els[i])
            when (curr) {
                is ArrayList<*> -> {
                    if (curr.size < els[i].toInt()) { println("ARR: '$soFar' does not exist!") }
                    curr = curr[els[i].toInt()]!!
                }
                is MutableMap<*, *> -> {
                    if (! curr.containsKey(els[i])) {
                        println("OBJ: '$soFar' does not exist")
                    }
                    curr = curr[els[i]]!!
                }
                // is String -> {
                //     curr = curr
                // }
            }
        }
        return curr
    }

    fun parseToMap(jsonString: String): MutableMap<String, Any> {
        val jsonElement: JsonElement = Json.parseToJsonElement(jsonString)
        val map = mutableMapOf<String, Any>()
        recurse(map, ".", jsonElement)
        return map
    }

    private fun recurse(map: MutableMap<String, Any>, key: String, jsonElement: JsonElement, arrList: MutableList<Any>? = null) {
        when (jsonElement) {
            is JsonArray -> {
                val newList = mutableListOf<Any>()
                if (arrList == null) {
                    map[key] = newList
                } else {
                    arrList.add(newList)
                }
                jsonElement.forEach {
                    recurse(mutableMapOf("ARRAY" to "ARRAYKONTEXT"), "ARRAY", it, newList)
                }
            }
            is JsonObject -> {
                val objMap = mutableMapOf<String, Any>()
                if (arrList == null) {
                    map[key] = objMap
                } else {
                    arrList.add(objMap)
                }
                jsonElement.keys.forEach {
                    recurse(objMap, it, jsonElement.getOrElse(it) {JsonPrimitive("UNKNOWN")})
                }
            }
            is JsonPrimitive -> {
                if (arrList == null) {
                    map[key] = jsonElement.toString()
                } else {
                    arrList.add(jsonElement.toString())
                }
            }
            else -> {
                throw Exception("unknown JSON Type for: $jsonElement")
            }
        }
    }
}
