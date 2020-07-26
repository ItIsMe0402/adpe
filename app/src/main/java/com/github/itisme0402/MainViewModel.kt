package com.github.itisme0402

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.itisme0402.content.UsersRepository
import com.github.itisme0402.entity.User
import io.reactivex.internal.disposables.SequentialDisposable

class MainViewModel constructor(
    private val usersRepository: UsersRepository,
    private val schedulerHolder: SchedulerHolder
) : ViewModel() {

    private val _tabIndexLiveData = MutableLiveData<Int>().apply { value = 0 }
    val tabIndexLiveData: LiveData<Int> = _tabIndexLiveData
    var tabIndex: Int
        get() = _tabIndexLiveData.value!!
        set(value) {
            _tabIndexLiveData.value = value
        }

    private val _usersStateLiveData = MutableLiveData<State<List<User>>>()
    val usersStateLiveData: LiveData<State<List<User>>> = _usersStateLiveData

    private val loadUsersDisposable = SequentialDisposable()

    override fun onCleared() {
        loadUsersDisposable.dispose()
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
}
