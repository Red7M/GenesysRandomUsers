package com.genesys.challenge.redamehali.datasource.remote

import android.annotation.SuppressLint
import android.util.Log
import com.genesys.challenge.redamehali.api.RandomUserApi
import com.genesys.challenge.redamehali.models.Users
import com.genesys.challenge.redamehali.Result
import kotlinx.coroutines.*

/**
 * RemoteDataSource class responsible for remote requests.
 * @param randomUserApi Instance of the api used to call http get requests.
 */
class RandomUserRemoteDataSource(private val randomUserApi: RandomUserApi) {

    @SuppressLint("LongLogTag")
    val handler = CoroutineExceptionHandler { _, exception->
        Log.i(TAG, "CoroutineExceptionHandler got $exception")
    }

    /**
     * Gets users using api request call in a worker thread coroutine context
     * @param seed Keyword allows generating the same set of users. E.g: the seed "foobar" will always
     * return results for Becky Sims (for version 1.0)
     * @param fetchCount The fetch count of users
     *
     * @return returns results from random user api call
     */
    @SuppressLint("LongLogTag")
    suspend fun getUsers(seed: String, fetchCount: Int): Result<Users> =
        withContext(Dispatchers.IO + handler) {
            supervisorScope {
                val usersDeferred = async { randomUserApi.getUsers(fetchCount, seed) }
                try {
                    val usersResponse = usersDeferred.await() ?: return@supervisorScope Result.Error(
                        Exception("Users not found")
                    )
                    Log.i(TAG, "getUsers: users successfully retrieved")
                    Log.i(TAG, "list contains ${usersResponse.userList.size}")
                    return@supervisorScope Result.Success(usersResponse)
                } catch (e: Exception) {
                    return@supervisorScope Result.Error(e)
                }
            }
        }

    /**
     * Gets users using api request call in a worker thread coroutine context
     * @param fetchCount The fetch count of users
     * @param gender The gender to query for. E.g: Male returns ONLY male users, and vice versa.
     *
     * @return returns results from random user api call by gender
     */
    @SuppressLint("LongLogTag")
    suspend fun getUsersByGender(fetchCount: Int, gender: String): Result<Users> =
        withContext(Dispatchers.IO + handler) {
            supervisorScope {
                val usersDeferred = async { randomUserApi.getUsersByGender(fetchCount, gender) }
                try {
                    val usersResponse = usersDeferred.await() ?: return@supervisorScope Result.Error(
                        Exception("Users not found")
                    )
                    Log.i(TAG, "getUsersByGender: users successfully retrieved")
                    Log.i(TAG, "list contains ${usersResponse.userList.size}")
                    return@supervisorScope Result.Success(usersResponse)
                } catch (e: Exception) {
                    return@supervisorScope Result.Error(e)
                }
            }
        }

    companion object {
        const val TAG = "RandomUserRemoteDataSource"
    }
}