package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.listener.RecordArrayListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.IntersectMerge
import com.aerospike.skyhook.util.Merge
import com.aerospike.skyhook.util.UnionMerge
import io.netty.channel.ChannelHandlerContext

abstract class SmergeBaseCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordArrayListener, Merge {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 2) { argValidationErrorMsg(cmd) }

        aeroCtx.client.get(
            null, this, null,
            getKeys(cmd).toTypedArray()
        )
    }

    private fun getKeys(cmd: RequestCommand): List<Key> {
        return cmd.args.drop(1)
            .map { createKey(Value.get(it)) }
    }

    override fun onSuccess(keys: Array<out Key>?, records: Array<Record?>?) {
        if (records == null) {
            writeEmptyList(ctx)
        } else {
            val values = merge(records.filterNotNull()
                .map { it.getMap(aeroCtx.bin) }.map { it.keys })
            writeResponse(values)
        }
        ctx.flush()
    }
}

class SinterCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : SmergeBaseCommandListener(aeroCtx, ctx), IntersectMerge

class SunionCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : SmergeBaseCommandListener(aeroCtx, ctx), UnionMerge
