package com.genesys.challenge.redamehali.api

import com.genesys.challenge.redamehali.models.Users
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Api class responsible for executing get request from RandomUser API
 */
interface RandomUserApi {

    /**
     * Executes an HTTPS GET request call to RandomUser API using results and seed as query
     * @param results The fetch count of users
     * @param seed Keyword allows generating the same set of users. E.g: the seed "foobar" will always
     * return results for Becky Sims (for version 1.0)
     *
     * @return Returns a [com.genesys.challenge.redamehali.models.Users] pojo or null if it fails.
     */
    @GET("api/")
    suspend fun getUsers(
        @Query("results") results: Int,
        @Query("seed") seed: String
    ): Users?

    /**
     * Executes an HTTPS GET request call to RandomUser API using results and gender as query
     * @param results The fetch count of users
     * @param gender The gender to query for. E.g: Male returns ONLY male users, and vice versa.
     *
     * @return Returns a [com.genesys.challenge.redamehali.models.Users] pojo or null if it fails.
     */
    @GET("api/")
    suspend fun getUsersByGender(
        @Query("results") results: Int,
        @Query("gender") gender: String
    ): Users?
}