package com.github.itisme0402

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.itisme0402.content.CommentsRepository
import com.github.itisme0402.content.PostsRepository
import com.github.itisme0402.content.UsersRepository
import com.github.itisme0402.entity.Comment
import com.github.itisme0402.entity.Post
import com.github.itisme0402.entity.User
import io.reactivex.internal.disposables.SequentialDisposable
import kotlin.math.min

class MainViewModel constructor(
    private val usersRepository: UsersRepository,
    private val postsRepository: PostsRepository,
    private val commentsRepository: CommentsRepository,
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

    private var chosenPostId: Long? = null

    private val commentsStateLiveData = MutableLiveData<State<List<Comment>>>()
        .apply {
            observeForever {
                firstIndexLiveData.value = 0
            }
        }
    private val firstIndexLiveData = MutableLiveData<Int>()
    val pagedCommentsStateLiveData: LiveData<State<CommentPage>> =
        MediatorLiveData<State<CommentPage>>()
            .apply {
                fun mergeStateWithPage() {
                    val commentsState = commentsStateLiveData.value
                    val startIndex = firstIndexLiveData.value
                    if (commentsState == null || startIndex == null) {
                        return
                    }
                    value = when (commentsState) {
                        is State.Loaded -> {
                            val comments = commentsState.content
                            val totalComments = comments.size
                            val endIndexEx = min(comments.size, startIndex + COMMENTS_PER_PAGE)
                            val pageContent = comments.subList(startIndex, endIndexEx)
                            val text = if (comments.isEmpty()) {
                                ""
                            } else {
                                "${startIndex + 1} to $endIndexEx of $totalComments"
                            }
                            State.Loaded(
                                CommentPage(pageContent, text)
                            )
                        }
                        State.Loading -> State.Loading
                        is State.Error -> commentsState
                    }
                }

                addSource(commentsStateLiveData) { mergeStateWithPage() }
                addSource(firstIndexLiveData) { mergeStateWithPage() }
            }

    data class CommentPage(
        val pageContent: List<Comment>,
        val footerText: String
    )

    private val loadCommentsDisposable = SequentialDisposable()

    override fun onCleared() {
        loadUsersDisposable.dispose()
        loadPostsDisposable.dispose()
        loadCommentsDisposable.dispose()
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

    fun onPostChosen(postId: Long) {
        if (chosenPostId != postId) {
            chosenPostId = postId
            loadCommentsDisposable.update(null)
            commentsStateLiveData.value = null
            firstIndexLiveData.value = null
        }
        loadCommentsToChosenPost()
        tabIndex = TAB_COMMENTS
    }

    fun loadComments() {
        if (chosenPostId == null) {
            chosenPostId = (_postsStateLiveData.value as? State.Loaded)
                ?.content
                ?.firstOrNull()
                ?.id
        }
        loadCommentsToChosenPost()
    }

    private fun loadCommentsToChosenPost() {
        val postId = chosenPostId ?: return
        when (commentsStateLiveData.value) {
            null,
            is State.Error -> {
                commentsStateLiveData.value = State.Loading
                commentsRepository.loadCommentsToPost(postId)
                    .backgroundToMain(schedulerHolder)
                    .subscribe(
                        { comments ->
                            commentsStateLiveData.value = State.Loaded(comments)
                        },
                        { e ->
                            commentsStateLiveData.value = State.Error(e.message ?: "")
                        }
                    )
                    .storeTo(loadCommentsDisposable)
            }
        }
    }

    fun prevPageOfComments() = movePage(-1)

    fun nextPageOfComments() = movePage(+1)

    private fun movePage(num: Int) {
        val oldIndex = firstIndexLiveData.value
        val comments = (commentsStateLiveData.value as? State.Loaded)?.content
        if (oldIndex != null && comments != null) {
            val newIndex = (oldIndex + num * COMMENTS_PER_PAGE)
                .coerceAtLeast(0)
            if (newIndex in 0..comments.lastIndex) {
                firstIndexLiveData.value = newIndex
            }
        }
    }

    companion object {
        const val TAB_USERS = 0
        const val TAB_POSTS = 1
        const val TAB_COMMENTS = 2
        const val COMMENTS_PER_PAGE = 3
    }
}
