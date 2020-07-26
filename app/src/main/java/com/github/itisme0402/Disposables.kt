package com.github.itisme0402

import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.SequentialDisposable

@Suppress("NOTHING_TO_INLINE")
inline fun Disposable.storeTo(container: SequentialDisposable): Disposable {
    container.update(this)
    return this
}
