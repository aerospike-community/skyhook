package com.aerospike.skyhook.listener.scan

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

open class HscanCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    @Volatile
    protected lateinit var scanCommand: ScanCommand

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        scanCommand = ScanCommand(cmd, 3)
        client.operate(
            null, this, null,
            key, getOperation()
        )
    }

    protected open fun getOperation(): Operation {
        return MapOperation.getByIndexRange(
            aeroCtx.bin,
            scanCommand.cursor.toInt(),
            scanCommand.COUNT.toInt(),
            MapReturnType.KEY_VALUE
        )
    }

    override fun onSuccess(key: Key?, record: Record?) {
        try {
            if (record == null) {
                writeArrayHeader(2)
                writeSimpleString(ScanCommand.zeroCursor)
                writeEmptyList()
            } else {
                val asList = record.bins[aeroCtx.bin] as List<*>
                writeArrayHeader(2)
                writeSimpleString(getNextCursor(asList.size))
                writeElementsArray(asList)
            }
            flushCtxTransactionAware()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    protected open fun writeElementsArray(list: List<*>) {
        writeObjectListStr(list
            .map { it as Map.Entry<*, *> }
            .map { it.toPair().toList() }.flatten()
        )
    }

    private fun getNextCursor(responseSize: Int): String {
        return if (responseSize < scanCommand.COUNT) {
            ScanCommand.zeroCursor
        } else {
            (scanCommand.cursor.toInt() + responseSize).toString()
        }
    }
}
