package com.github.itisme0402.content

import com.github.itisme0402.network.NetworkApi

class CommentsRepositoryImpl(
    private val networkApi: NetworkApi
) : CommentsRepository {

    override fun loadCommentsToPost(postId: Long) = networkApi.commentsToPost(postId)
}
