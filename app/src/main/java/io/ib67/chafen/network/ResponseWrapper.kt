package io.ib67.chafen.network

import kotlinx.serialization.Serializable

@Serializable
data class ResponseWrapper<T>(
    val success: Boolean,
    val code: Int,
    val data: T
)
