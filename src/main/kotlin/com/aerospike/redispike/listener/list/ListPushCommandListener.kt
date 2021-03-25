package com.aerospike.redispike.listener.list

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.ListOperation
import com.aerospike.client.listener.RecordListener
import com.aerospike.client.policy.WritePolicy
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class ListPushCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    private data class OpWritePolicy(val writePolicy: WritePolicy, val op: Operation)

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val opPolicy = getOpWritePolicy(cmd)
        val operations = arrayOf(
            opPolicy.op
        )
        aeroCtx.client.operate(
            null, this, opPolicy.writePolicy,
            key, *operations
        )
    }

    private fun getOpWritePolicy(cmd: RequestCommand): OpWritePolicy {
        val values = getValues(cmd)
        return when (cmd.command) {
            RedisCommand.LPUSH -> {
                OpWritePolicy(
                    defaultWritePolicy,
                    ListOperation.insertItems(aeroCtx.bin, 0, values)
                )
            }
            RedisCommand.LPUSHX -> {
                OpWritePolicy(
                    updateOnlyPolicy,
                    ListOperation.insertItems(aeroCtx.bin, 0, values)
                )
            }
            RedisCommand.RPUSH -> {
                OpWritePolicy(
                    defaultWritePolicy,
                    ListOperation.appendItems(aeroCtx.bin, values)
                )
            }
            RedisCommand.RPUSHX -> {
                OpWritePolicy(
                    updateOnlyPolicy,
                    ListOperation.appendItems(aeroCtx.bin, values)
                )
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }

    private fun getValues(cmd: RequestCommand): List<Value> {
        return cmd.args!!.drop(2).map {
            Typed.getValue(it)
        }
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullArray(ctx)
            ctx.flush()
        } else {
            try {
                writeResponse(record.bins[aeroCtx.bin]!!)
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
