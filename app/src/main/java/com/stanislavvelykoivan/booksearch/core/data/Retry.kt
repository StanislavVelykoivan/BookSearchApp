package com.stanislavvelykoivan.booksearch.core.data

import com.stanislavvelykoivan.booksearch.core.domain.Error
import com.stanislavvelykoivan.booksearch.core.domain.Result
import kotlinx.coroutines.delay

suspend fun <T, E : Error> retry(
    attempts: Int = 3,
    delayMillis: Long = 600,
    block: suspend () -> Result<T, E>
): Result<T, E>{
    repeat(attempts - 1){
        val result = block()

        if (result is Result.Success)
            return result

        delay(delayMillis)
    }
    return block()
}