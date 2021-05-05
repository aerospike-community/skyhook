package com.aerospike.skyhook.util.client

import com.aerospike.client.AerospikeClient
import com.aerospike.client.Host
import com.aerospike.client.IAerospikeClient
import com.aerospike.client.policy.ClientPolicy
import com.aerospike.skyhook.config.ServerConfiguration
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import mu.KotlinLogging
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AerospikeClientPoolImpl @Inject constructor(
    private val config: ServerConfiguration,
    private val clientPolicy: ClientPolicy
) : AerospikeClientPool {

    companion object {
        private val log = KotlinLogging.logger {}

        const val defaultAerospikePort = 3000
        private const val clientPoolSize = 8L
    }

    private val clientPool: Cache<String, IAerospikeClient> =
        CacheBuilder.newBuilder().maximumSize(clientPoolSize).build()

    private val defaultClient: IAerospikeClient by lazy {
        createClient(clientPolicy)
    }

    override fun getClient(authDetails: AuthDetails): IAerospikeClient? {
        val key = authDetails.hashString
        return Optional.ofNullable(clientPool.getIfPresent(key)).orElseGet {
            val policy = ClientPolicy(clientPolicy)
            policy.user = authDetails.user
            policy.password = authDetails.password

            try {
                val client = createClient(policy)
                log.info("Cache a new AerospikeClient")
                clientPool.put(key, client)
                client
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun getClient(authDetailsHash: String?): IAerospikeClient {
        return authDetailsHash?.let { clientPool.getIfPresent(it) } ?: defaultClient
    }

    private fun createClient(policy: ClientPolicy): IAerospikeClient {
        return AerospikeClient(
            policy,
            *Host.parseHosts(config.hostList, defaultAerospikePort)
        )
    }
}
