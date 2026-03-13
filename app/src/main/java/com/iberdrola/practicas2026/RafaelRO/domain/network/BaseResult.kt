package com.iberdrola.practicas2026.RafaelRO.domain.network

sealed class BaseResult <out T> {
    data class Sucess <T> (val data : T) : BaseResult<T>()
    data class Error (val exception: InvokeException) : BaseResult<Nothing>()
}