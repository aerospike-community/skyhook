package com.aerospike.skyhook.listener.scan

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.ScanCallback
import com.aerospike.client.cluster.Node
import com.aerospike.client.cluster.Partition
import com.aerospike.client.exp.Exp
import com.aerospike.client.policy.ScanPolicy
import com.aerospike.client.query.KeyRecord
import com.aerospike.client.query.PartitionFilter
import com.aerospike.client.query.RegexFlag
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.listener.scan.ScanCommand.Companion.zeroCursor
import io.netty.channel.ChannelHandlerContext

class ScanCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx) {

    private lateinit var scanCommand: ScanCommand
    private var currentPartition = 0
    private var count = 0
    private val recordSet: RecordSet by lazy {
        RecordSet()
    }

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 2) { argValidationErrorMsg(cmd) }

        scanCommand = ScanCommand(cmd, 2)

        scanPartition()
        writeScanResponse()
    }

    private fun writeScanResponse() {
        writeArrayHeader(2)
        writeSimpleString(getNextCursor())
        writeObjectListStr(recordSet.map { it.key.userKey.`object` as String })
        flushCtxTransactionAware()
    }

    private fun getNextCursor(): String {
        return if (recordSet.size < scanCommand.COUNT) {
            zeroCursor
        } else {
            recordSet.nextCursor() ?: zeroCursor
        }
    }

    private fun scanPartition() {
        val scanPolicy = buildScanPolicy()
        var filter = getPartitionFilter()
        while (isScanRequired()) {
            client.scanPartitions(
                scanPolicy, filter, aeroCtx.namespace, aeroCtx.set,
                callback, aeroCtx.bin
            )
            resetMaxRecords(scanPolicy)
            filter = PartitionFilter.id(++currentPartition)
        }
    }

    private fun resetMaxRecords(scanPolicy: ScanPolicy) {
        scanPolicy.maxRecords = if (scanCommand.COUNT > 0) {
            scanCommand.COUNT - count
        } else {
            scanCommand.COUNT
        }
    }

    private fun buildScanPolicy(): ScanPolicy {
        val scanPolicy = ScanPolicy()
        scanPolicy.sendKey = true
        scanPolicy.includeBinData = false
        scanPolicy.maxRecords = scanCommand.COUNT
        val expressions = listOfNotNull(
            scanCommand.TYPE?.let {
                Exp.eq(
                    Exp.stringBin(aeroCtx.typeBin),
                    Exp.`val`(it.str)
                )
            },
            scanCommand.TYPE?.let {
                Exp.regexCompare(
                    scanCommand.MATCH,
                    RegexFlag.ICASE or RegexFlag.NEWLINE,
                    Exp.key(Exp.Type.STRING)
                )
            }
        )
        scanPolicy.filterExp = when (expressions.size) {
            0 -> null
            1 -> Exp.build(expressions.first())
            else -> Exp.build(Exp.and(*expressions.toTypedArray()))
        }
        return scanPolicy
    }

    private fun getPartitionFilter(): PartitionFilter? {
        if (scanCommand.cursor != zeroCursor) {
            val key = Key(aeroCtx.namespace, aeroCtx.set, scanCommand.cursor)
            currentPartition = Partition.getPartitionId(key.digest)
            return PartitionFilter.after(key)
        }
        return PartitionFilter.id(currentPartition)
    }

    private fun isScanRequired(): Boolean {
        return (scanCommand.COUNT == 0L || count < scanCommand.COUNT) && isValidPartition()
    }

    private fun isValidPartition(): Boolean {
        return currentPartition >= 0 && currentPartition < Node.PARTITIONS
    }

    private val callback = ScanCallback { key: Key?, record: Record? ->
        recordSet.add(KeyRecord(key, record))
        count++
    }
}
