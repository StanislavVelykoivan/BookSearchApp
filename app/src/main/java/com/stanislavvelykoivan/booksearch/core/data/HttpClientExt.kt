package com.stanislavvelykoivan.booksearch.core.data

import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.ensureActive
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext
import kotlin.reflect.typeOf

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Remote> {
    val response = try {
        execute()
    }
    catch (e: HttpRequestTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: ConnectTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: SocketTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: UnresolvedAddressException) {
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: UnknownHostException) {
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        coroutineContext.ensureActive()
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    return respondToResult(response)
}

suspend inline fun <reified T> respondToResult(
    response: HttpResponse
): Result<T, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                if (typeOf<T>() == typeOf<Unit>()) {
                    Result.Success(Unit as T)
                } else {
                    Result.Success(response.body<T>())
                }
            } catch (e: Exception) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        503 -> Result.Error(DataError.Remote.SERVICE_UNAVAILABLE)
        in 500..599 -> Result.Error(DataError.Remote.SERVER)
        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
}