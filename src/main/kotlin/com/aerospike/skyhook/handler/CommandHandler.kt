package com.aerospike.skyhook.handler

import com.aerospike.skyhook.command.RequestCommand

interface CommandHandler {

    /**
     * Handles the incoming request command.
     */
    fun handle(cmd: RequestCommand)
}
