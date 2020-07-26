package com.github.itisme0402

import io.reactivex.Scheduler
import io.reactivex.Single

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Single<T>.backgroundToMain(schedulerHolder: SchedulerHolder): Single<T> {
    return this
        .subscribeOn(schedulerHolder.backgroundScheduler)
        .observeOn(schedulerHolder.mainThreadScheduler)
}

class SchedulerHolder(
    val backgroundScheduler: Scheduler,
    val mainThreadScheduler: Scheduler
)
