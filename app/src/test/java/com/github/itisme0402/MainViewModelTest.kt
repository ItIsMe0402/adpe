package com.github.itisme0402

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.itisme0402.content.UsersRepository
import com.github.itisme0402.entity.User
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var usersRepository: UsersRepository
    private val loadSomeUsersCall = SingleSubject.create<List<User>>()
    private lateinit var sut: MainViewModel

    @Before
    fun setUp() {
        `when`(usersRepository.loadSomeUsers()).thenReturn(loadSomeUsersCall)
        sut = MainViewModel(
            usersRepository,
            SchedulerHolder(Schedulers.trampoline(), Schedulers.trampoline())
        )
    }

    @Test
    fun loadSomeUsers_shouldStartLoading() {
        sut.loadSomeUsers()
        assertTrue(loadSomeUsersCall.hasObservers())
    }

    @Test
    fun loadSomeUsers_shouldShowLoadingProgress() {
        sut.loadSomeUsers()
        assertEquals(State.Loading, sut.usersStateLiveData.value)
    }

    @Test
    fun loadSomeUsers_shouldEmitUsers_onSuccess() {
        sut.loadSomeUsers()
        @Suppress("UNCHECKED_CAST") val users = mock(List::class.java) as List<User>
        loadSomeUsersCall.onSuccess(users)
        assertEquals(State.Loaded(users), sut.usersStateLiveData.value)
    }

    @Test
    fun loadSomeUsers_shouldEmitError_onError() {
        sut.loadSomeUsers()
        val msg = "t3st"
        val e = Exception(msg)
        loadSomeUsersCall.onError(e)
        assertEquals(State.Error(msg), sut.usersStateLiveData.value)
    }

    @Test
    fun loadSomeUsers_shouldNotStartLoading_whenAlreadyLoading() {
        sut.loadSomeUsers()
        sut.loadSomeUsers()
        verify(usersRepository).loadSomeUsers()
    }
}
