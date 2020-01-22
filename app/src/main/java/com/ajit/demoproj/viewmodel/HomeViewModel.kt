package com.ajit.demoproj.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource.PostsDataSource
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource.PostDataSourceFactory
import com.ajit.demoproj.data.api.ApiPostResp
import com.ajit.demoproj.data.local.Post
import com.ajit.demoproj.data.repository.PostRepository

import com.ajit.demoproj.data.datasource.NetworkState
import com.ajit.demoproj.util.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable


class HomeViewModel(
    private val postRepository: PostRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {


    var postList: LiveData<PagedList<Post>>

    private val compositeDisposable = CompositeDisposable()

    private val pageSize = 20

    private val sourceFactory: PostDataSourceFactory

    init {

        sourceFactory = PostDataSourceFactory(compositeDisposable, postRepository)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        postList = LivePagedListBuilder<Long, Post>(sourceFactory, config).build()

    }

    fun getPersistentData(): LiveData<PagedList<Post>> {
        return postRepository.getPersistentData()
    }

    fun retry() {
        sourceFactory.postsDataSourceLiveData.value!!.retry()
    }

    fun refresh() {
        sourceFactory.postsDataSourceLiveData.value!!.invalidate()
    }

    fun getNetworkState(): LiveData<NetworkState> =
        Transformations.switchMap<PostsDataSource, NetworkState>(
            sourceFactory.postsDataSourceLiveData
        ) { it.networkState }


    fun getNetworkData(): LiveData<ApiPostResp> =
        Transformations.switchMap<PostsDataSource, ApiPostResp>(
            sourceFactory.postsDataSourceLiveData
        ) { it.apiRespData }


    fun getRefreshState(): LiveData<NetworkState> =
        Transformations.switchMap<PostsDataSource, NetworkState>(
            sourceFactory.postsDataSourceLiveData
        ) { it.initialLoad }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}
