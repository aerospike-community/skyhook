package com.aerospike.skyhook.handler

import com.aerospike.skyhook.command.RequestCommand

interface CommandHandler {
    fun handle(cmd: RequestCommand)
}
