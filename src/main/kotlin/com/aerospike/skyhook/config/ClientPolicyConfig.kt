package com.aerospike.skyhook.config

import com.aerospike.client.policy.AuthMode

/**
 * Aerospike Java Client [com.aerospike.client.policy.ClientPolicy] configuration properties.
 */
data class ClientPolicyConfig(

    /**
     * User authentication to cluster.
     * Leave null for clusters running without restricted access.
     */
    val user: String? = null,

    /**
     * Password authentication to cluster.
     * The password will be stored by the client and sent to server in hashed format.
     * Leave null for clusters running without restricted access.
     */
    val password: String? = null,

    /**
     * Expected cluster name. If not null, server nodes must return this cluster name in order to join
     * the client's view of the cluster. Should only be set when connecting to servers that support the
     * "cluster-name" info command.
     */
    val clusterName: String? = null,

    /**
     * Authentication mode used when user/password is defined.
     */
    val authMode: AuthMode? = null,

    /**
     * Initial host connection timeout in milliseconds.
     * The timeout when opening a connection to the server host for the first time.
     */
    val timeout: Int? = null,

    /**
     * Login timeout in milliseconds. The timeout is used when user authentication is enabled
     * and a node login is being performed.
     */
    val loginTimeout: Int? = null,

    /**
     * Minimum number of asynchronous connections allowed per server node.
     * Preallocate min connections on client node creation. The client will periodically allocate new connections
     * if count falls below min connections.
     */
    val asyncMinConnsPerNode: Int? = null,

    /**
     * Maximum number of asynchronous connections allowed per server node.
     * Transactions will go through retry logic and potentially fail with "ResultCode.NO_MORE_CONNECTIONS"
     * if the maximum number of connections would be exceeded.
     */
    val asyncMaxConnsPerNode: Int? = null,

    /**
     * Throw exception if all seed connections fail on cluster instantiation.
     */
    val failIfNotConnected: Boolean? = null,

    /**
     * Should use "services-alternate" instead of "services" in info request during cluster tending.
     * "services-alternate" returns server configured external IP addresses that client uses to talk to nodes.
     */
    val useServicesAlternate: Boolean? = null,
)
