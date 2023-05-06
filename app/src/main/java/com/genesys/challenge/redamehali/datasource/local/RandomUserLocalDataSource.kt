package com.genesys.challenge.redamehali.datasource.local

import com.genesys.challenge.redamehali.Result
import com.genesys.challenge.redamehali.models.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * LocalDataSource class responsible for non-remote computations.
 */
class RandomUserLocalDataSource {

    /**
     * Uses location data from users to returns countries represented, and list of users count from each country
     */
    suspend fun getCountriesAndUsersPerCountry(users: Users): Result<String> = withContext(Dispatchers.IO) {
        try {
            val countriesAndUsers = StringBuilder("Countries represented are: ")
            // Hashmap key represents the country, and value users count of that country.
            // If a country has only one user, then the map will be: e.g: (key,value) -> (nameOfCountry,1)
            val countriesUsersMap = HashMap<String, Int>()

            // Loops through users list
            for (user in users.userList) {
                // Gets country value
                val country = user.location.country
                // Check if country already exists in the map
                if (countriesUsersMap.contains(user.location.country)) {
                    val value = countriesUsersMap[country]
                    // Increments the user count per country match
                    countriesUsersMap[country] = value!!+ 1
                }
                // Sets value of country as "1" when it's a new country
                else {
                    countriesUsersMap[country] = 1
                }
            }

            // Appends necessary texts
            countriesAndUsers.append(countriesUsersMap.keys.joinToString(", "))
            countriesAndUsers.append("\n").append("There are: \n")
            countriesUsersMap.forEach {countriesAndUsers.append(it.value).append(" users from ").append(it.key).append(",\n") }

            return@withContext Result.Success(countriesAndUsers.toString())
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }
}