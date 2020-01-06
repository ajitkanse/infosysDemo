package com.ajit.demoproj.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource.UsersDataSource
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource.UsersDataSourceFactory
import com.ajit.demoproj.data.api.ApiResp
import com.ajit.demoproj.data.api.Row
import com.ajit.demoproj.data.repository.Repository

import com.ajit.demoproj.ui.datasource.NetworkState
import com.ajit.demoproj.util.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable


class HomeViewModel(
    private val repository: Repository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {


    var list: LiveData<PagedList<Row>>

    private val compositeDisposable = CompositeDisposable()

    private val pageSize = 15

    private val sourceFactory: UsersDataSourceFactory

    init {
        sourceFactory = UsersDataSourceFactory(compositeDisposable, repository)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        list = LivePagedListBuilder<Long, Row>(sourceFactory, config).build()

    }

    fun retry() {
        sourceFactory.usersDataSourceLiveData.value!!.retry()
    }

    fun refresh() {
        sourceFactory.usersDataSourceLiveData.value!!.invalidate()
    }

    fun getNetworkState(): LiveData<NetworkState> = Transformations.switchMap<UsersDataSource, NetworkState>(
        sourceFactory.usersDataSourceLiveData
    ) { it.networkState }


    fun getNetworkData(): LiveData<ApiResp> = Transformations.switchMap<UsersDataSource, ApiResp>(
        sourceFactory.usersDataSourceLiveData
    ) { it.apiRespData }


    fun getRefreshState(): LiveData<NetworkState> = Transformations.switchMap<UsersDataSource, NetworkState>(
        sourceFactory.usersDataSourceLiveData
    ) { it.initialLoad }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}
