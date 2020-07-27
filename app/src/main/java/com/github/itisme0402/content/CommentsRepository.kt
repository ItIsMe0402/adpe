package com.github.itisme0402.content

import com.github.itisme0402.entity.Comment
import io.reactivex.Single

interface CommentsRepository {
    fun loadCommentsToPost(postId: Long): Single<List<Comment>>
}
