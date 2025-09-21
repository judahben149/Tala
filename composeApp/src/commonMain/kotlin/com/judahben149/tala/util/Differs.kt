package com.judahben149.tala.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * Diff two serializable objects (works in KMP commonMain).
 */
inline fun <reified T> diffObjects(labelA: String, a: T, labelB: String, b: T, serializer: KSerializer<T>) {
    val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    val mapA = json.encodeToJsonElement(serializer, a).jsonObject
    val mapB = json.encodeToJsonElement(serializer, b).jsonObject

    val diffs = buildList {
        for (key in (mapA.keys + mapB.keys)) {
            val aVal = mapA[key]
            val bVal = mapB[key]
            if (aVal != bVal) {
                add("$key: $labelA=$aVal | $labelB=$bVal")
            }
        }
    }

//    if (diffs.isEmpty()) {
//        println("✅ No differences between $labelA and $labelB (${T::class.simpleName})")
//    } else {
//        println("⚠️ Differences in ${T::class.simpleName}:")
//        diffs.forEach { println("   $it") }
//    }
}

fun diffJson(labelA: String, a: JsonObject, labelB: String, b: JsonObject) {
    val diffs = buildList {
        for (key in (a.keys + b.keys)) {
            val aVal = a[key]
            val bVal = b[key]
            if (aVal != bVal) add("$key: $labelA=$aVal | $labelB=$bVal")
        }
    }
//    if (diffs.isEmpty()) {
//        println("✅ No differences between $labelA and $labelB")
//    } else {
//        println("⚠️ Differences:")
//        diffs.forEach { println("   $it") }
//    }
}

