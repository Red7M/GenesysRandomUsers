package com.genesys.challenge.redamehali.utils

import android.os.Bundle
import androidx.core.os.bundleOf
import com.genesys.challenge.redamehali.models.Location

/**
 * UI helper class used throughout the project
 */
class UiUtil {

    companion object {

        /**
         * Appends title, firstname, and lastname respectively and returns the concatenated result
         *
         * @param title Title of a user, e.g: "Mr", "Ms", ..ect
         * @param firstname First name of a user
         * @param lastname Last name of a user
         *
         * @return returns a concatenated result of title, firstname, and lastname respectively
         */
        fun appendName(title: String?, firstname: String, lastname: String): StringBuilder {
            return if (title.isNullOrBlank()) StringBuilder(firstname).append(" ").append(lastname)
            else StringBuilder(title).append(". ").append(firstname).append(" ").append(lastname)
        }

        /**
         * Appends location by concatenating city and country respectively
         *
         * @param location Location object contains location data
         *
         * @return returns a concatenated result of city + ", " + country
         */
        fun appendLocation(location: Location) : StringBuilder {
            return StringBuilder(location.city).append(", ").append(location.country)
        }
    }
}