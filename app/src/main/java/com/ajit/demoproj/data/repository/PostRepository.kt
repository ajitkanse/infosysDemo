package com.ajit.demoproj.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ajit.demoproj.data.api.ApiPostResp
import com.ajit.demoproj.data.api.ApiService
import com.ajit.demoproj.data.local.Post
import com.ajit.demoproj.data.local.PostDao
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton
import io.reactivex.functions.Action


@Singleton
class PostRepository @Inject constructor(private val apiService: ApiService, private val dao: PostDao) {

    fun getPostDataFromApi(): Single<ApiPostResp> = apiService.getPostDataFromApi()

    fun getPersistentData(): LiveData<PagedList<Post>> =
        LivePagedListBuilder(dao.getAllRowsPagedList(), 20).build()

    fun updateUser(data: List<Post>) {
        Completable.fromAction(object : Action {
            @Throws(Exception::class)
            override fun run() {
                dao.insertAllRow(data)
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    println("userlist complette}")
                }

                override fun onError(e: Throwable) {
                }
            })
    }
}


