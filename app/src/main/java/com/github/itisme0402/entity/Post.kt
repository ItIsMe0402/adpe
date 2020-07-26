package com.github.itisme0402.entity

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: Long,
    @SerializedName("body")
    val body: String
)
