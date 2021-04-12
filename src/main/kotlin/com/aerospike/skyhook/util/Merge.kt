package com.aerospike.skyhook.util

interface Merge {
    fun merge(data: List<Set<Any?>>): Set<Any>
}

interface UnionMerge : Merge {
    override fun merge(data: List<Set<Any?>>): Set<Any> {
        return data.flatten().filterNotNull().toSet()
    }
}

interface IntersectMerge : Merge {
    override fun merge(data: List<Set<Any?>>): Set<Any> {
        if (data.isEmpty()) {
            return setOf()
        }
        return data.reduce { acc, l -> acc.intersect(l.filterNotNull()) }
            .filterNotNull().toSet()
    }
}
