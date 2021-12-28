package com.aerospike.skyhook.util

import com.aerospike.client.Info
import com.aerospike.client.cluster.Node
import java.util.regex.Pattern

object InfoUtils {

    fun getNamespaceInfo(ns: String, node: Node): Map<String, String> {
        val schemaInfo = Info.request(null, node, "namespace/$ns")
        return schemaInfo.split(";")
            .associate { it.split("=".toRegex(), 2).toPair() }
    }

    fun getSetInfo(ns: String, set: String?, node: Node): Map<String, String> {
        val sets = Info.request(null, node, "sets")
        val tableInfo = sets.split(";")
            .filter { it.startsWith("ns=$ns:set=$set") }[0]
        return Pattern.compile("\\s*:\\s*").split(tableInfo).toList()
            .filterNotNull().associate { it.split("=".toRegex(), 2).toPair() }
    }
}
