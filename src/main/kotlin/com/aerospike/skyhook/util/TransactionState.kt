package com.aerospike.skyhook.util

import com.aerospike.client.Key
import com.aerospike.skyhook.command.RequestCommand
import java.util.*
import java.util.concurrent.ExecutorService

class TransactionState(val pool: ExecutorService) {
    var inTransaction: Boolean = false
        private set

    var transactionId: String? = null
        private set

    val commands: LinkedList<RequestCommand> = LinkedList()
    val keys: LinkedHashSet<Key> = LinkedHashSet()

    fun startTransaction() {
        inTransaction = true
        transactionId = UUID.randomUUID().toString()
    }

    fun clear() {
        inTransaction = false
        transactionId = null
        commands.clear()
        keys.clear()
    }
}
