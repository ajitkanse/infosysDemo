package com.storiyoh.demoproj.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.ajit.demoproj.data.local.Post
import io.reactivex.disposables.CompositeDisposable

class FakeUsersDataSourceFactory(private val compositeDisposable: CompositeDisposable,
                                 private val repository: FakeRepository
)
    : DataSource.Factory<Long, Post>() {

    val usersDataSourceLiveData = MutableLiveData<FakeUsersDataSource>()

    override fun create(): DataSource<Long, Post> {
        val usersDataSource = FakeUsersDataSource(repository, compositeDisposable)
        usersDataSourceLiveData.postValue(usersDataSource)
        return usersDataSource
    }

}