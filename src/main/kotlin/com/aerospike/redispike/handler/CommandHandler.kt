package com.aerospike.redispike.handler

import com.aerospike.redispike.command.RequestCommand

interface CommandHandler {
    fun handle(cmd: RequestCommand)
}
