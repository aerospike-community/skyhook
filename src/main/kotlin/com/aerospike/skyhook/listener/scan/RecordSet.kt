package com.aerospike.skyhook.listener.scan

import com.aerospike.client.query.KeyRecord
import java.util.*

class RecordSet() : LinkedList<KeyRecord>() {

    private var lastRecord: KeyRecord? = null

    fun nextCursor(): String? {
        return lastRecord?.key?.userKey?.`object`?.let { it as String }
    }

    override fun add(element: KeyRecord): Boolean {
        this.lastRecord = element
        return super.add(element)
    }
}
