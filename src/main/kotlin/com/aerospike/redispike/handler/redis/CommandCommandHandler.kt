package com.aerospike.redispike.handler.redis

import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.handler.CommandHandler
import com.aerospike.redispike.handler.NettyResponseWriter
import com.aerospike.redispike.listener.BaseListener
import io.netty.channel.ChannelHandlerContext
import java.util.*

class CommandCommandHandler(
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 1) { BaseListener.argValidationErrorMsg(cmd) }

        if (cmd.argCount == 1) {
            RedisCommand.writeCommand(ctx)
        } else {
            when (String(cmd.args!![1]).toUpperCase(Locale.ENGLISH)) {
                "COUNT" -> {
                    writeLong(ctx, RedisCommand.totalCommands)
                }
                "INFO" -> {
                    val commands = cmd.args.drop(2).map { String(it) }
                        .map { it.toLowerCase(Locale.ENGLISH) }
                    RedisCommand.writeCommandInfo(ctx, commands)
                }
                else -> {
                    throw IllegalArgumentException(cmd.command.toString())
                }
            }
        }
        ctx.flush()
    }
}
