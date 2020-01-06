package com.storiyoh.demoproj.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.ajit.demoproj.data.api.Row
import com.ajit.demoproj.data.repository.Repository
import io.reactivex.disposables.CompositeDisposable

class FakeUsersDataSourceFactory(private val compositeDisposable: CompositeDisposable,
                                 private val repository: FakeRepository
)
    : DataSource.Factory<Long, Row>() {

    val usersDataSourceLiveData = MutableLiveData<FakeUsersDataSource>()

    override fun create(): DataSource<Long, Row> {
        val usersDataSource = FakeUsersDataSource(repository, compositeDisposable)
        usersDataSourceLiveData.postValue(usersDataSource)
        return usersDataSource
    }

}