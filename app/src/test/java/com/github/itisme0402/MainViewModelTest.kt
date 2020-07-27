package com.github.itisme0402

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.itisme0402.content.PostsRepository
import com.github.itisme0402.content.UsersRepository
import com.github.itisme0402.entity.Company
import com.github.itisme0402.entity.Post
import com.github.itisme0402.entity.User
import io.reactivex.Single
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
    @Mock
    private lateinit var postsRepository: PostsRepository
    private val loadSomeUsersCall = SingleSubject.create<List<User>>()
    private val loadPostsByUserCall = SingleSubject.create<List<Post>>()
    private lateinit var sut: MainViewModel

    @Before
    fun setUp() {
        `when`(usersRepository.loadSomeUsers()).thenReturn(loadSomeUsersCall)
        sut = MainViewModel(
            usersRepository,
            postsRepository,
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

    @Test
    fun onUserChosen_shouldStartLoading() {
        `when`(postsRepository.loadPostsByUser(TEST_USER_ID)).thenReturn(loadPostsByUserCall)
        sut.onUserChosen(TEST_USER_ID)
        assertTrue(loadPostsByUserCall.hasObservers())
    }

    @Test
    fun onUserChosen_shouldEmitPosts_onSuccess() {
        @Suppress("UNCHECKED_CAST") val posts = mock(List::class.java) as List<Post>
        `when`(postsRepository.loadPostsByUser(TEST_USER_ID)).thenReturn(Single.just(posts))
        sut.onUserChosen(TEST_USER_ID)
        assertEquals(State.Loaded(posts), sut.postsStateLiveData.value)
    }

    @Test
    fun loadPosts_shouldNotReload_whenPostsLoadedForChosenUser() {
        @Suppress("UNCHECKED_CAST") val posts = mock(List::class.java) as List<Post>
        `when`(postsRepository.loadPostsByUser(TEST_USER_ID)).thenReturn(Single.just(posts))
        sut.onUserChosen(TEST_USER_ID)
        clearInvocations(postsRepository)
        sut.loadPosts()
        verify(postsRepository, never()).loadPostsByUser(anyLong())
    }

    @Test
    fun loadPosts_shouldPickFirstUser_whenNoUserChosen() {
        sut.loadSomeUsers()
        loadSomeUsersCall.onSuccess(
            listOf(
                User(TEST_USER_ID, "John Doe", Company("[classified]"), "j.doe@example.com")
            )
        )
        val loadPostsByUserCall = SingleSubject.create<List<Post>>()
        `when`(postsRepository.loadPostsByUser(TEST_USER_ID)).thenReturn(loadPostsByUserCall)
        sut.loadPosts()
        assertTrue(loadPostsByUserCall.hasObservers())
    }

    companion object {
        private const val TEST_USER_ID = 309L
    }
}
