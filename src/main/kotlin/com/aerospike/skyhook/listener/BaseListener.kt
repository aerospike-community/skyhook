package com.aerospike.skyhook.listener

import com.aerospike.client.*
import com.aerospike.client.exp.Exp
import com.aerospike.client.policy.RecordExistsAction
import com.aerospike.client.policy.WritePolicy
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.aeroCtxAttrKey
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.authDetailsAttrKey
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.clientPoolAttrKey
import com.aerospike.skyhook.util.TransactionState
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging
import java.io.IOException

abstract class BaseListener(
    ctx: ChannelHandlerContext
) : NettyResponseWriter(ctx), CommandHandler {

    companion object {

        @JvmStatic
        val log = KotlinLogging.logger {}

        @JvmStatic
        fun argValidationErrorMsg(cmd: RequestCommand): String {
            return "${cmd.command} arguments"
        }

        const val transactionTimeoutMillis = 1000L
    }

    protected val updateOnlyPolicy by lazy {
        val updateOnlyPolicy = getWritePolicy()
        updateOnlyPolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY
        updateOnlyPolicy
    }

    protected val createOnlyPolicy by lazy {
        val updateOnlyPolicy = getWritePolicy()
        updateOnlyPolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY
        updateOnlyPolicy
    }

    protected val defaultWritePolicy: WritePolicy by lazy {
        getWritePolicy()
    }

    protected val transactionState: TransactionState by lazy {
        ctx.channel().attr(AerospikeChannelInitializer.transactionAttrKey).get()
    }

    protected fun getWritePolicy(): WritePolicy {
        val writePolicy = WritePolicy()
        writePolicy.sendKey = true

        // transaction awareness
        writePolicy.filterExp = Exp.build(
            Exp.or(
                Exp.not(Exp.binExists(aeroCtx.transactionIdBin)),
                Exp.and(
                    Exp.binExists(aeroCtx.transactionIdBin),
                    Exp.or(
                        Exp.gt(Exp.sinceUpdate(), Exp.`val`(transactionTimeoutMillis)),
                        Exp.eq(
                            Exp.bin(aeroCtx.transactionIdBin, Exp.Type.STRING),
                            Exp.`val`(transactionState.transactionId ?: "")
                        )
                    )
                )
            )
        )
        return writePolicy
    }

    protected fun systemBins(): Array<Bin> {
        return listOfNotNull(transactionState.transactionId?.let {
            Bin(aeroCtx.transactionIdBin, it)
        }).toTypedArray()
    }

    protected fun systemBins(type: ValueType): Array<Bin> {
        return systemBins() + Bin(aeroCtx.typeBin, type.str)
    }

    protected fun systemOps(): Array<Operation> {
        return systemBins().map {
            Operation.put(it)
        }.toTypedArray()
    }

    protected fun systemOps(type: ValueType): Array<Operation> {
        return systemOps() + Operation.put(Bin(aeroCtx.typeBin, type.str))
    }

    protected val aeroCtx: AerospikeContext by lazy {
        ctx.channel().attr(aeroCtxAttrKey).get()
    }

    protected val client: IAerospikeClient by lazy {
        ctx.channel().attr(clientPoolAttrKey).get().getClient(
            ctx.channel().attr(authDetailsAttrKey).get()
        )
    }

    @Throws(IOException::class)
    protected open fun writeResponse(mapped: Any?) {
        writeObject(mapped)
    }

    @Throws(IOException::class)
    protected open fun writeError(e: AerospikeException?) {
        writeErrorString("Internal error")
    }

    open fun onFailure(exception: AerospikeException?) {
        try {
            log.debug { exception }
            writeError(exception)
            flushCtxTransactionAware()
        } catch (e: IOException) {
            closeCtx(e)
        }
    }

    protected fun createKey(key: Value): Key {
        val aeroKey = Key(aeroCtx.namespace, aeroCtx.set, key)
        if (transactionState.inTransaction) {
            transactionState.keys.add(aeroKey)
        }
        return aeroKey
    }

    protected fun createKey(key: ByteArray): Key {
        return createKey(Value.get(String(key)))
    }
}
