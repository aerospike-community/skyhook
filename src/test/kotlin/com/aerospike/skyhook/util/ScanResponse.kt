package com.aerospike.skyhook.util

data class ScanResponse(
    val cursor: String,
    val elements: List<String>
)
