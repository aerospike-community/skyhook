package com.aerospike.skyhook.handler.redis

import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext
import java.util.*

class CommandCommandHandler(
    ctx: ChannelHandlerContext
) : NettyResponseWriter(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 1) { BaseListener.argValidationErrorMsg(cmd) }

        if (cmd.argCount == 1) {
            RedisCommand.writeCommand(ctx)
        } else {
            when (String(cmd.args[1]).uppercase(Locale.ENGLISH)) {
                "COUNT" -> {
                    writeLong(RedisCommand.totalCommands)
                }
                "INFO" -> {
                    val commands = cmd.args.drop(2).map { String(it) }
                        .map { it.lowercase(Locale.ENGLISH) }
                    RedisCommand.writeCommandInfo(ctx, commands)
                }
                else -> {
                    throw IllegalArgumentException(cmd.command.toString())
                }
            }
        }
        flushCtxTransactionAware()
    }
}
