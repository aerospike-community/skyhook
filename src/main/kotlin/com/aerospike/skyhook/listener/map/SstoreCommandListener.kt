package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.listener.RecordArrayListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.IntersectMerge
import com.aerospike.skyhook.util.Merge
import com.aerospike.skyhook.util.Typed
import com.aerospike.skyhook.util.UnionMerge
import io.netty.channel.ChannelHandlerContext

abstract class SstoreBaseCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordArrayListener, Merge {

    @Volatile
    private lateinit var key: Key

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }

        key = createKey(cmd.key)
        client.get(
            null, this, null,
            getKeys(cmd).toTypedArray()
        )
    }

    private fun getKeys(cmd: RequestCommand): List<Key> {
        return cmd.args.drop(2)
            .map { createKey(it) }
    }

    override fun onSuccess(keys: Array<out Key>?, records: Array<Record?>?) {
        if (records == null) {
            writeLong(0L)
        } else {
            val values = merge(records.filterNotNull()
                .map { it.getMap(aeroCtx.bin) }.map { it.keys })

            val operation = MapOperation.putItems(
                MapPolicy(),
                aeroCtx.bin,
                values.map {
                    Typed.getValue(it.toString().toByteArray()) to Value.getAsNull()
                }.toMap()
            )
            client.operate(
                defaultWritePolicy, key, setTypeOp(), operation
            )
            writeLong(values.size)
        }
        flushCtxTransactionAware()
    }
}

class SinterstoreCommandListener(
    ctx: ChannelHandlerContext
) : SstoreBaseCommandListener(ctx), IntersectMerge

class SunionstoreCommandListener(
    ctx: ChannelHandlerContext
) : SstoreBaseCommandListener(ctx), UnionMerge
