package com.github.itisme0402

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.itisme0402.content.PostsRepository
import com.github.itisme0402.content.UsersRepository
import com.github.itisme0402.entity.Post
import com.github.itisme0402.entity.User
import io.reactivex.internal.disposables.SequentialDisposable

class MainViewModel constructor(
    private val usersRepository: UsersRepository,
    private val postsRepository: PostsRepository,
    private val schedulerHolder: SchedulerHolder
) : ViewModel() {

    private val _tabIndexLiveData = MutableLiveData<Int>().apply { value = TAB_USERS }
    val tabIndexLiveData: LiveData<Int> = _tabIndexLiveData
    var tabIndex: Int
        get() = _tabIndexLiveData.value!!
        set(value) {
            _tabIndexLiveData.value = value
        }

    private val _usersStateLiveData = MutableLiveData<State<List<User>>>()
    val usersStateLiveData: LiveData<State<List<User>>> = _usersStateLiveData

    private val loadUsersDisposable = SequentialDisposable()

    private var chosenUserId: Long? = null

    private val _postsStateLiveData = MutableLiveData<State<List<Post>>>()
    val postsStateLiveData: LiveData<State<List<Post>>> = _postsStateLiveData

    private val loadPostsDisposable = SequentialDisposable()

    override fun onCleared() {
        loadUsersDisposable.dispose()
        loadPostsDisposable.dispose()
        super.onCleared()
    }

    fun loadSomeUsers() {
        when (_usersStateLiveData.value) {
            null,
            is State.Error -> {
                _usersStateLiveData.value = State.Loading
                usersRepository.loadSomeUsers()
                    .backgroundToMain(schedulerHolder)
                    .subscribe(
                        { users ->
                            _usersStateLiveData.value = State.Loaded(users)
                        },
                        { e ->
                            _usersStateLiveData.value = State.Error(e.message ?: "")
                        }
                    )
                    .storeTo(loadUsersDisposable)
            }
        }
    }

    fun onUserChosen(userId: Long) {
        if (chosenUserId != userId) {
            chosenUserId = userId
            loadPostsDisposable.update(null)
            _postsStateLiveData.value = null
        }
        loadPostsByChosenUser()
        tabIndex = TAB_POSTS
    }

    fun loadPosts() {
        if (chosenUserId == null) {
            chosenUserId = (_usersStateLiveData.value as? State.Loaded)
                ?.content
                ?.firstOrNull()
                ?.id
        }
        loadPostsByChosenUser()
    }

    private fun loadPostsByChosenUser() {
        val userId = chosenUserId ?: return
        when (_postsStateLiveData.value) {
            null,
            is State.Error -> {
                _postsStateLiveData.value = State.Loading
                postsRepository.loadPostsByUser(userId)
                    .backgroundToMain(schedulerHolder)
                    .subscribe(
                        { posts ->
                            _postsStateLiveData.value = State.Loaded(posts)
                        },
                        { e ->
                            _postsStateLiveData.value = State.Error(e.message ?: "")
                        }
                    )
                    .storeTo(loadPostsDisposable)
            }
        }
    }

    companion object {
        const val TAB_USERS = 0
        const val TAB_POSTS = 1
    }
}
