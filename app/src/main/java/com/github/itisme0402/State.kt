package com.github.itisme0402

sealed class State<out T> {
    data class Loaded<out T>(val content: T) : State<T>()
    object Loading : State<Nothing>()
    data class Error(val message: String) : State<Nothing>()
}
