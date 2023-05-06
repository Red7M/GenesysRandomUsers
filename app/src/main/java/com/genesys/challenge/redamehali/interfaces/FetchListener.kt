package com.genesys.challenge.redamehali.interfaces

/**
 * Interface listens for fetch, and countries and user per country results
 */
interface FetchListener {
    fun onFetchResult()
    fun getCountries()
}