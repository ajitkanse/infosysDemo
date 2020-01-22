package com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource


import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

import com.ajit.demoproj.data.local.Post
import com.ajit.demoproj.data.repository.PostRepository
import io.reactivex.disposables.CompositeDisposable


class PostDataSourceFactory(private val compositeDisposable: CompositeDisposable,
                            private val postRepository: PostRepository
)
    : DataSource.Factory<Long, Post>() {

    val postsDataSourceLiveData = MutableLiveData<PostsDataSource>()

    override fun create(): DataSource<Long, Post> {
        val postsDataSource = PostsDataSource(postRepository, compositeDisposable)
        postsDataSourceLiveData.postValue(postsDataSource)
        return postsDataSource
    }

}
