package com.github.itisme0402.network

import com.github.itisme0402.entity.Comment
import com.github.itisme0402.entity.Post
import com.github.itisme0402.entity.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {
    @GET("/users")
    fun users(): Single<List<User>>

    @GET("/posts")
    fun postsByUser(@Query("userId") userId: Long): Single<List<Post>>

    @GET("/comments")
    fun commentsToPost(@Query("postId") postId: Long): Single<List<Comment>>
}
