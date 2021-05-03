package com.aerospike.skyhook.util.client

import com.google.common.hash.Hashing

data class AuthDetails(
    val user: String,
    val password: String
) {
    @Suppress("UnstableApiUsage")
    val hashString: String by lazy {
        Hashing.sha256()
            .hashBytes(toString().encodeToByteArray())
            .toString()
    }
}
