package com.github.itisme0402.content

import com.github.itisme0402.entity.Post
import io.reactivex.Single

interface PostsRepository {
    fun loadPostsByUser(userId: Long): Single<List<Post>>
}
