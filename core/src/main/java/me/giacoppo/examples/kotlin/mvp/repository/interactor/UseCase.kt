package me.giacoppo.examples.kotlin.mvp.repository.interactor

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import me.giacoppo.examples.kotlin.mvp.repository.interactor.executor.PostExecutionThread
import me.giacoppo.examples.kotlin.mvp.repository.interactor.executor.ThreadExecutor

/**
 * Created by Peppe on 17/06/2017.
 */
abstract class UseCase<T, Params>(private val threadExecutor: ThreadExecutor, private val postExecutionThread : PostExecutionThread) {
    private val disposables = CompositeDisposable()

    abstract fun buildUseCaseObservable(params: Params) : Observable<T>

    fun execute(observer: DisposableObserver<T>, params: Params): Unit {
        val observable = buildUseCaseObservable(params)
                .subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler())

        addDisposable(observable.subscribeWith(observer))
    }

    fun dispose(): Unit {
        if (!disposables.isDisposed)
            disposables.dispose()
    }

    private fun addDisposable(disposable: Disposable): Unit {
        disposables.add(disposable)
    }
}