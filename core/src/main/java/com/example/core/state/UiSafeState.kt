package com.example.core.state

sealed class UiSafeState<out T> {
    object Uninitialized : UiSafeState<Nothing>()
    object Loading : UiSafeState<Nothing>()
    object Empty: UiSafeState<Nothing>()
    object ErrorConnection : UiSafeState<Nothing>()
    data class Error(val message: String, val errorCode: Int = 0) : UiSafeState<Nothing>()
    data class Success<out T>(val data: T) : UiSafeState<T>()
}

fun <T> UiSafeState<T>.isError(withEmpty: Boolean = true): Boolean =
    this is UiSafeState.Error ||
            this is UiSafeState.ErrorConnection ||
            (this is UiSafeState.Empty && withEmpty)