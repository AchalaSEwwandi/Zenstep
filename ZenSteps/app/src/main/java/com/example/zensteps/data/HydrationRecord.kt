package com.example.zensteps.data

import java.io.Serializable

data class HydrationRecord(
    val id: String = "",
    val amount: Int = 0, // in ml
    val timestamp: Long = System.currentTimeMillis()
) : Serializable