package com.aerospike.redispike.util

object SystemUtils {

    enum class OS {
        LINUX, MAC, WINDOWS, OTHER
    }

    val os: OS by lazy {
        val os = System.getProperty("os.name").toLowerCase()
        when {
            os.contains("nux") -> {
                OS.LINUX
            }
            os.contains("mac") -> {
                OS.MAC
            }
            os.contains("win") -> {
                OS.WINDOWS
            }
            else -> OS.OTHER
        }
    }
}
