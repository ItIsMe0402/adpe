package com.github.itisme0402.entity

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("company")
    val company: Company,
    @SerializedName("email")
    val email: String
)
