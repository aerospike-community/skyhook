package com.aerospike.skyhook.util

import com.aerospike.skyhook.command.RequestCommand
import java.util.*
import java.util.concurrent.ExecutorService

class TransactionState(val pool: ExecutorService) {
    var inTransaction: Boolean = false
        private set

    val commands: LinkedList<RequestCommand> = LinkedList()

    fun startTransaction() {
        inTransaction = true
    }

    fun clear() {
        inTransaction = false
        commands.clear()
    }
}
