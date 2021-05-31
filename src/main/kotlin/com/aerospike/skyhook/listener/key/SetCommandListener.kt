package com.aerospike.skyhook.listener.key

import com.aerospike.client.*
import com.aerospike.client.listener.RecordListener
import com.aerospike.client.policy.RecordExistsAction
import com.aerospike.client.policy.WritePolicy
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.listener.ValueType
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext
import java.lang.Integer.max
import java.util.*

class SetCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    private class SetCommand(val cmd: RequestCommand) {
        var EX: Int? = null
        var PX: Int? = null
        var EXAT: Long? = null
            private set
        var PXAT: Long? = null
            private set
        var KEEPTTL: Boolean = false
            private set
        var NX: Boolean = false
        var XX: Boolean = false
            private set
        var GET: Boolean = false
            private set

        lateinit var value: Value

        init {
            if (cmd.command == RedisCommand.SET) {
                for (i in 3 until cmd.args.size) {
                    setFlag(i)
                }
                validate()
            }
        }

        fun buildPolicy(policy: WritePolicy): WritePolicy {
            EX?.let {
                require(it > 0) { "invalid expiration" }
                policy.expiration = it
            }
            PX?.let {
                require(it > 0) { "invalid expiration" }
                policy.expiration = max(it / 1000, 1)
            }
            EXAT?.let {
                val exp = it - (System.currentTimeMillis() / 1000)
                require(exp > 0) { "invalid expiration" }
                policy.expiration = exp.toInt()
            }
            PXAT?.let {
                val exp = it - System.currentTimeMillis()
                require(exp > 0) { "invalid expiration" }
                policy.expiration = max(exp.toInt() / 1000, 1)
            }
            if (NX) {
                policy.recordExistsAction = RecordExistsAction.CREATE_ONLY
            } else if (XX) {
                policy.recordExistsAction = RecordExistsAction.UPDATE_ONLY
            }
            return policy
        }

        private fun setFlag(i: Int) {
            val flagStr = String(cmd.args[i])
            when (flagStr.toUpperCase()) {
                "EX" -> EX = String(cmd.args[i + 1]).toInt()
                "PX" -> PX = String(cmd.args[i + 1]).toInt()
                "EXAT" -> EXAT = String(cmd.args[i + 1]).toLong()
                "PXAT" -> PXAT = String(cmd.args[i + 1]).toLong()
                "KEEPTTL" -> KEEPTTL = true
                "NX" -> NX = true
                "XX" -> XX = true
                "GET" -> GET = true
            }
        }

        private fun validate() {
            require(listOfNotNull(EX, PX, EXAT, PXAT).size <= 1) { "[EX|PX|EXAT|PXAT]" }
            require(!(NX && XX)) { "[NX|XX]" }
            require(!(NX && GET)) { "[NX|GET]" }
        }
    }

    @Volatile
    private lateinit var setCommand: SetCommand

    override fun handle(cmd: RequestCommand) {
        parseCommand(cmd)
        val key = createKey(cmd.key)
        val writePolicy = setCommand.buildPolicy(getWritePolicy())

        client.operate(null, this, writePolicy, key, *buildOps())
    }

    override fun onSuccess(key: Key?, record: Record?) {
        try {
            when {
                setCommand.cmd.command == RedisCommand.SETNX -> {
                    writeLong(1L)
                }
                setCommand.GET -> {
                    if (record == null) writeNullString()
                    else writeBulkString(record.getString(aeroCtx.bin))
                }
                else -> {
                    writeOK()
                }
            }
            flushCtxTransactionAware()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    override fun writeError(e: AerospikeException?) {
        when {
            setCommand.cmd.command == RedisCommand.SETNX -> {
                writeLong(0L)
            }
            setCommand.XX -> {
                writeNullString()
            }
            else -> {
                writeErrorString("Internal error")
            }
        }
    }

    private fun parseCommand(cmd: RequestCommand) {
        setCommand = SetCommand(cmd)
        when (cmd.command) {
            RedisCommand.SET -> {
                require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }

                setCommand.value = Typed.getValue(cmd.args[2])
            }
            RedisCommand.SETNX -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                setCommand.NX = true
                setCommand.value = Typed.getValue(cmd.args[2])
            }
            RedisCommand.SETEX -> {
                require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

                setCommand.EX = Typed.getInteger(cmd.args[2])
                setCommand.value = Typed.getValue(cmd.args[3])
            }
            RedisCommand.PSETEX -> {
                require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

                setCommand.PX = Typed.getInteger(cmd.args[2])
                setCommand.value = Typed.getValue(cmd.args[3])
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }

    private fun buildOps(): Array<Operation> {
        val ops = LinkedList(
            listOf(
                Operation.put(Bin(aeroCtx.bin, setCommand.value)),
                *systemOps(ValueType.STRING)
            )
        )
        if (setCommand.GET) {
            ops.push(Operation.get(aeroCtx.bin))
        }
        return ops.toTypedArray()
    }
}
