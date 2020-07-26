package com.github.itisme0402.content

import com.github.itisme0402.entity.User
import io.reactivex.Single

interface UsersRepository {
    fun loadSomeUsers(): Single<List<User>>
}
