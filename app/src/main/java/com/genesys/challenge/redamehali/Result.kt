package com.genesys.challenge.redamehali

/**
 * Results class responsible of bundling type variables in a Success data class
 * or Exceptions and failures in an Error data class
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
