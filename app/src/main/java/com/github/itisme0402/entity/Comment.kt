package com.github.itisme0402.entity

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("body")
    val body: String
)
