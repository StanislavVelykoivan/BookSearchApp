package com.stanislavvelykoivan.booksearch.core.presentation

import com.stanislavvelykoivan.booksearch.R
import com.stanislavvelykoivan.booksearch.core.domain.DataError


fun DataError.toUiText(): UiText {
    val stringRes = when (this) {
        DataError.Local.NO_APP_TO_OPEN_FILE -> R.string.error_no_app_to_open_file
        DataError.Local.DISK_FULL -> R.string.error_disk_full
        DataError.Local.UNKNOWN -> R.string.error_unknow
        DataError.Remote.REQUEST_TIMEOUT -> R.string.error_request_timeout
        DataError.Remote.TOO_MANY_REQUESTS -> R.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> R.string.error_no_internet
        DataError.Remote.SERVER -> R.string.error_server_inner
        DataError.Remote.SERIALIZATION -> R.string.error_serialization
        DataError.Remote.UNKNOWN -> R.string.error_unknow
        DataError.Remote.SERVICE_UNAVAILABLE -> R.string.error_service_unavailable
    }
    return UiText.StringResourceId(stringRes)
}