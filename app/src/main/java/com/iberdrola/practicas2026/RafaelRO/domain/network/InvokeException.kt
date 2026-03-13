package com.iberdrola.practicas2026.RafaelRO.domain.network

sealed class InvokeException {
    object NetworkError : InvokeException()
    object ServerError : InvokeException()
    object DatabaseError : InvokeException()
    object FileError : InvokeException()
    data class UnknownError(val erroMsg: String?) : InvokeException()
}