package com.peterwachira.cashipay

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
