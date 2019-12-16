package com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource


import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

import com.ajit.demoproj.data.api.Row
import com.ajit.demoproj.data.repository.Repository
import io.reactivex.disposables.CompositeDisposable


class UsersDataSourceFactory(private val compositeDisposable: CompositeDisposable,
                             private val repository: Repository
)
    : DataSource.Factory<Long, Row>() {

    val usersDataSourceLiveData = MutableLiveData<UsersDataSource>()

    override fun create(): DataSource<Long, Row> {
        val usersDataSource = UsersDataSource(repository, compositeDisposable)
        usersDataSourceLiveData.postValue(usersDataSource)
        return usersDataSource
    }

}
