package com.aerospike.skyhook.listener.scan

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.ScanCallback
import com.aerospike.client.exp.Exp
import com.aerospike.client.policy.ScanPolicy
import com.aerospike.client.query.KeyRecord
import com.aerospike.client.query.RegexFlag
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.RegexUtils
import io.netty.channel.ChannelHandlerContext

class KeysCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx) {

    private lateinit var regexString: String
    private val recordSet: RecordSet by lazy {
        RecordSet()
    }

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }
        regexString = RegexUtils.format(String(cmd.args[1]))

        scan()
        writeScanResponse()
    }

    private fun scan() {
        client.scanAll(buildScanPolicy(), aeroCtx.namespace, aeroCtx.set, callback)
    }

    private fun buildScanPolicy(): ScanPolicy {
        val scanPolicy = ScanPolicy()
        scanPolicy.sendKey = true
        scanPolicy.includeBinData = false

        scanPolicy.filterExp = Exp.build(
            Exp.regexCompare(
                regexString,
                RegexFlag.ICASE or RegexFlag.NEWLINE,
                Exp.key(Exp.Type.STRING)
            )
        )
        return scanPolicy
    }

    private fun writeScanResponse() {
        writeObjectListStr(recordSet.map { it.key.userKey.`object` as String })
        flushCtxTransactionAware()
    }

    private val callback = ScanCallback { key: Key?, record: Record? ->
        recordSet.add(KeyRecord(key, record))
    }
}
