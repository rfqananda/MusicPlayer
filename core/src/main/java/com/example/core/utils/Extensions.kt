package com.example.core.utils

import com.example.core.wrapper.NetworkResultWrapper
import com.google.gson.JsonSyntaxException
import javax.net.ssl.SSLPeerUnverifiedException

fun <Input, Output> processResponse(
    result: NetworkResultWrapper<Input>,
    successBlock: (Input) -> DomainResult.Success<Output>,
): DomainResult<Output> {
    return when (result) {
        is NetworkResultWrapper.Error -> errorMapperWithout400(result)
        is NetworkResultWrapper.Exception -> exceptionMapper(result)
        is NetworkResultWrapper.Success -> successBlock(result.data)
    }
}

fun <Input, Output> errorMapperWithout400(result: NetworkResultWrapper.Error<Output>): DomainResult<Input> {
    val emptyStateErrorCodes = listOf(422, 404, 403, 409, 406, 401)

    return if (emptyStateErrorCodes.contains(result.code)) DomainResult.EmptyState(result.message)
    else DomainResult.ErrorState(result.message)
}

fun <Input, Output> exceptionMapper(result: NetworkResultWrapper.Exception<Output>): DomainResult<Input> {
    return if (result.throwable is SSLPeerUnverifiedException) {
        DomainResult.TechnicalError(SSL_ERROR_CONST)
    } else if (result.throwable is JsonSyntaxException) {
        DomainResult.TechnicalError(RESPONSE_ERROR_CONST)
    } else {
        DomainResult.NetworkError
    }
}