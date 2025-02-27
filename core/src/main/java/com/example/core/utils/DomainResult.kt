package com.example.core.utils

const val SSL_ERROR_CONST = 900
const val RESPONSE_ERROR_CONST = 901

sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class TechnicalError(val code: Int) : DomainResult<Nothing>()
    data class EmptyState(val message: String?, val responseStatusCode: Int? = 0) :
        DomainResult<Nothing>()

    data class ErrorState(val message: String?, val responseStatusCode: Int? = 0) :
        DomainResult<Nothing>()

    object NetworkError : DomainResult<Nothing>()
}
