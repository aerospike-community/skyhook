package com.aerospike.skyhook.util.client

import com.aerospike.client.IAerospikeClient

interface AerospikeClientPool {

    fun getClient(authDetails: AuthDetails): IAerospikeClient?

    fun getClient(authDetailsHash: String?): IAerospikeClient
}
