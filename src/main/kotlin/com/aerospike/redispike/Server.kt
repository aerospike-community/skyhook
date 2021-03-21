package com.aerospike.redispike

/**
 * The Server interface
 */
interface Server {
    /**
     * Start the server.
     */
    fun start()

    /**
     * Stop the server.
     */
    fun stop()
}
