package com.vodolazskiy.forecastapplication.domain.exceptions

sealed class ServiceError(cause: Throwable? = null) : Exception(cause) {
    class NoLocationException(cause: Throwable? = null) : Exception(cause)
    class ApiError(cause: Throwable? = null, reason: String? = null) : Exception(reason, cause)
}

