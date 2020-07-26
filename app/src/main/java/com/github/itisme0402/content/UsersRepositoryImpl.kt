package com.github.itisme0402.content

import com.github.itisme0402.entity.User
import com.github.itisme0402.network.NetworkApi
import io.reactivex.Single

class UsersRepositoryImpl(
    private val networkApi: NetworkApi
) : UsersRepository {

    override fun loadSomeUsers(): Single<List<User>> = networkApi.users()
}
