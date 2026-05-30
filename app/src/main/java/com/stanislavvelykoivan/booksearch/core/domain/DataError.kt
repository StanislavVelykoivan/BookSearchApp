package com.stanislavvelykoivan.booksearch.core.domain

sealed interface DataError: Error {
    enum class Remote: DataError{
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN,
        SERVICE_UNAVAILABLE
    }
    enum class Local: DataError{
        DISK_FULL,
        UNKNOWN
    }
}