package com.aerospike.skyhook.listener.key

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.client.policy.WritePolicy
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.listener.ValueType
import io.netty.channel.ChannelHandlerContext
import java.util.*

open class GetCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        client.get(null, this, defaultWritePolicy, key)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record?.bins == null) {
            writeNullString()
            flushCtxTransactionAware()
        } else {
            try {
                val value = record.bins[aeroCtx.bin]
                writeResponse(
                    when (value) {
                        is Long -> value.toString()
                        else -> value
                    }
                )
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}

class GetexCommandListener(
    ctx: ChannelHandlerContext
) : GetCommandListener(ctx) {

    private class GetexCommand(val cmd: RequestCommand) {
        var EX: Int? = null
        var PX: Int? = null
        var EXAT: Long? = null
            private set
        var PXAT: Long? = null
            private set
        var PERSIST: Boolean? = null
            private set

        init {
            for (i in 2 until cmd.args.size) {
                setFlag(i)
            }
            validate()
        }

        fun buildPolicy(policy: WritePolicy): WritePolicy {
            EX?.let {
                require(it > 0) { "invalid expiration" }
                policy.expiration = it
            }
            PX?.let {
                require(it > 0) { "invalid expiration" }
                policy.expiration = Integer.max(it / 1000, 1)
            }
            EXAT?.let {
                val exp = it - (System.currentTimeMillis() / 1000)
                require(exp > 0) { "invalid expiration" }
                policy.expiration = exp.toInt()
            }
            PXAT?.let {
                val exp = it - System.currentTimeMillis()
                require(exp > 0) { "invalid expiration" }
                policy.expiration = Integer.max(exp.toInt() / 1000, 1)
            }
            PERSIST?.let {
                policy.expiration = 0
            }
            return policy
        }

        private fun setFlag(i: Int) {
            val flagStr = String(cmd.args[i])
            when (flagStr.uppercase(Locale.ENGLISH)) {
                "EX" -> EX = String(cmd.args[i + 1]).toInt()
                "PX" -> PX = String(cmd.args[i + 1]).toInt()
                "EXAT" -> EXAT = String(cmd.args[i + 1]).toLong()
                "PXAT" -> PXAT = String(cmd.args[i + 1]).toLong()
                "PERSIST" -> PERSIST = true
            }
        }

        private fun validate() {
            require(listOfNotNull(EX, PX, EXAT, PXAT, PERSIST).size <= 1) {
                "[EX|PX|EXAT|PXAT|PERSIST]"
            }
        }
    }

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 2) { argValidationErrorMsg(cmd) }

        val getexCommand = GetexCommand(cmd)
        val key = createKey(cmd.key)
        val ops = arrayOf(
            *systemOps(ValueType.STRING),
            Operation.get(aeroCtx.bin)
        )
        val writePolicy = getexCommand.buildPolicy(getWritePolicy())

        client.operate(null, this, writePolicy, key, *ops)
    }
}
