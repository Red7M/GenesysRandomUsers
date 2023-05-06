package com.genesys.challenge.redamehali.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genesys.challenge.redamehali.models.Users
import com.genesys.challenge.redamehali.Result
import com.genesys.challenge.redamehali.repositories.RandomUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model class responsible of the screen level state holder. Exposes state to the UI, and
 * in the same time encapsulates related business logic.
 */
@HiltViewModel
class RandomUserViewModel @Inject constructor(private val randomUserRepository: RandomUserRepository) : ViewModel() {

    private val _users = MutableSharedFlow<Result<Users>>()
    val users : SharedFlow<Result<Users>> = _users

    private val _countriesAndUsers = MutableSharedFlow<Result<String>>()
    val countriesAndUsers : SharedFlow<Result<String>> = _countriesAndUsers

    /**
     * Calls getUsers from the repository and emits returned result in the view model coroutine scope
     * @param seed Keyword allows generating the same set of users. E.g: the seed "foobar" will always
     * return results for Becky Sims (for version 1.0)
     * @param fetchCount The fetch count of users
     *
     */
    fun getUsers(seed: String, fetchCount: Int) {
        viewModelScope.launch {
            val users = randomUserRepository.getUsers(seed, fetchCount)
            _users.emit(users)
        }
    }

    /**
     * Calls getUsersByGender from the repository and emits returned result in the view model coroutine scope
     * @param fetchCount The fetch count of users
     * @param gender The gender to query for. E.g: Male returns ONLY male users, and vice versa.
     */
    fun getUsersByGender(fetchCount: Int, gender: String) {
        viewModelScope.launch {
            val users = randomUserRepository.getUsersByGender(fetchCount, gender)
            _users.emit(users)
        }
    }

    /**
     * Calls getCountriesAndUsersPerCountry from the repository and emits returned result in the view model coroutine scope
     * @param users Users data previously fetched
     */
    fun getCountriesAndUsersPerCountry(users: Users)  {
        viewModelScope.launch {
            val countriesAndUsers = randomUserRepository.getCountriesAndUsersPerCountry(users)
            _countriesAndUsers.emit(countriesAndUsers)
        }
    }
}