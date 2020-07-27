package com.github.itisme0402

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.itisme0402.content.*
import com.github.itisme0402.network.NetworkApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object InjectAllStuffViewModelFactory : ViewModelProvider.Factory {

    private val networkApi: NetworkApi
    private val usersRepository: UsersRepository
    private val postsRepository: PostsRepository
    private val commentsRepository: CommentsRepository
    private val schedulerHolder = SchedulerHolder(
        Schedulers.io(),
        AndroidSchedulers.mainThread()
    )

    init {
        val okHttpClient = OkHttpClient.Builder()
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        networkApi = retrofit.create(NetworkApi::class.java)
        usersRepository = UsersRepositoryImpl(networkApi)
        postsRepository = PostsRepositoryImpl(networkApi)
        commentsRepository = CommentsRepositoryImpl(networkApi)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (modelClass) {
            MainViewModel::class.java -> {
                MainViewModel(
                    usersRepository,
                    postsRepository,
                    commentsRepository,
                    schedulerHolder
                ) as T
            }
            else -> throw IllegalArgumentException("Unsupported VM class: $modelClass")
        }
    }
}
