package com.aerospike.skyhook.config

import com.aerospike.client.policy.AuthMode

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
     * Authentication mode used when user/password is defined.
     */
    val authMode: AuthMode? = null,

    /**
     * Initial host connection timeout in milliseconds.
     * The timeout when opening a connection to the server host for the first time.
     */
    val timeout: Int? = null
)
