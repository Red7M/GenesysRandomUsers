package com.genesys.challenge.redamehali.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    val info: Info,
    @field:SerializedName("results")  
    val userList: List<User>) : Parcelable