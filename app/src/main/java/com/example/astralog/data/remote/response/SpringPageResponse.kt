package com.example.astralog.data.remote.response

data class SpringPageResponse<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean,
    val size: Int,
    val number: Int
)