package com.genesys.challenge.redamehali.di

import com.genesys.challenge.redamehali.api.RandomUserApi
import com.genesys.challenge.redamehali.commons.Constant
import com.genesys.challenge.redamehali.datasource.local.RandomUserLocalDataSource
import com.genesys.challenge.redamehali.datasource.remote.RandomUserRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Class responsible for Dependency Injection Using Hilt/Dagger and classes instantiation
 */
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    /**
     * Instantiates RandomUserApi instance
     */
    @Provides
    fun provideRandomUserApi(): RandomUserApi {
        return createRandomUserApi()
    }

    /**
     * Instantiates RandomUserApi, and [RandomUserRemoteDataSource] instance
     */
    @Provides
    fun provideRandomUserRemoteDataSource() : RandomUserRemoteDataSource {
        return RandomUserRemoteDataSource(provideRandomUserApi())
    }

    /**
     * Instantiates [RandomUserLocalDataSource] instance
     */
    @Provides
    fun provideRandomUserLocalDataSource() : RandomUserLocalDataSource {
        return RandomUserLocalDataSource()
    }

    companion object {
        /**
         * Builds Retrofit build instance of type RandomUserApi
         */
        fun createRandomUserApi() : RandomUserApi {
            // OkHttp interceptor which logs request and response information
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(logger)
                .addInterceptor { it.proceed(it.request().newBuilder()
                    .addHeader("Accept", "application/json").build())}.build()

            // Create an implementation of the API endpoints defined by the service interface
            return Retrofit.Builder()
                .baseUrl(Constant.RANDOM_USER_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RandomUserApi::class.java)
        }
    }
}