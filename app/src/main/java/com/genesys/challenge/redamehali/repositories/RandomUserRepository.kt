package com.genesys.challenge.redamehali.repositories

import com.genesys.challenge.redamehali.datasource.remote.RandomUserRemoteDataSource
import com.genesys.challenge.redamehali.models.Users
import com.genesys.challenge.redamehali.Result
import com.genesys.challenge.redamehali.datasource.local.RandomUserLocalDataSource
import javax.inject.Inject

/**
 * Repository class isolate [RandomUserRemoteDataSource] and
 * [RandomUserLocalDataSource]data sources from rest of the project.
 */
class RandomUserRepository @Inject constructor(
    private val randomUserRemoteDataSource: RandomUserRemoteDataSource,
    private val randomUserLocalDataSource: RandomUserLocalDataSource
) {

    suspend fun getUsers(seed: String, fetchCount: Int) : Result<Users> = randomUserRemoteDataSource.getUsers(seed, fetchCount)

    suspend fun getUsersByGender(fetchCount: Int, gender: String) : Result<Users> = randomUserRemoteDataSource.getUsersByGender(fetchCount, gender)

    suspend fun getCountriesAndUsersPerCountry(users: Users) : Result<String> = randomUserLocalDataSource.getCountriesAndUsersPerCountry(users)
}