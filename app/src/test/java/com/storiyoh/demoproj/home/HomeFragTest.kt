package com.storiyoh.demoproj.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ajit.demoproj.data.local.Post
import com.ajit.demoproj.data.datasource.Status
import com.storiyoh.demoproj.repository.FakeRepository
import com.storiyoh.demoproj.repository.FakeUsersDataSourceFactory
import io.reactivex.disposables.CompositeDisposable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HomeFragTest {

    private lateinit var repository: FakeRepository

    private val pageSize = 15
    lateinit var list: LiveData<PagedList<Post>>

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    lateinit var sourceFactory: FakeUsersDataSourceFactory

    var post: Post = Post(
        title = "title ",
        description = "description ",
        imageHref = "image "
    )

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setupViewModel() {
        repository = FakeRepository()
        repository.addData(post)

        sourceFactory = FakeUsersDataSourceFactory(compositeDisposable, repository)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        list = LivePagedListBuilder<Long, Post>(sourceFactory, config).build()

    }

    @Test
    fun `check api response is not null`() {
        val apiResp = repository.getDataFromApi()
        Assert.assertNotNull(apiResp)

    }

    @Test
    fun retry() {
        sourceFactory.usersDataSourceLiveData.observeForever {
            Assert.assertNotNull(it)
        }
    }

    @Test
    fun refresh() {
        sourceFactory.usersDataSourceLiveData.observeForever {
            println("${it.apiRespData}")
            Assert.assertNotNull(it.invalidate())
        }
    }

    @Test
    fun getNetworkState(){
        sourceFactory.usersDataSourceLiveData.observeForever {
            println("${it.networkState}")
            Assert.assertNotNull(it.networkState)
            Assert.assertEquals(Status.SUCCESS,it.networkState)
        }
    }

    @Test
    fun `onCleared`() {
        Assert.assertNotNull(compositeDisposable.dispose())
    }



}

