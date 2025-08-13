package dev.sdkforge.pdf.core

interface Platform {
    val name: String
    val version: String
}

expect val currentPlatform: Platform
