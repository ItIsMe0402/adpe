package com.github.itisme0402.content

import com.github.itisme0402.entity.Post
import com.github.itisme0402.network.NetworkApi
import io.reactivex.Single

class PostsRepositoryImpl(
    private val networkApi: NetworkApi
) : PostsRepository {

    override fun loadPostsByUser(userId: Long): Single<List<Post>> = networkApi.postsByUser(userId)
}
