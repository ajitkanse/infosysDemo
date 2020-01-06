package com.storiyoh.demoproj.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource.UsersDataSource
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource.UsersDataSourceFactory
import com.ajit.demoproj.data.api.ApiResp
import com.ajit.demoproj.data.api.Row
import com.ajit.demoproj.data.repository.Repository
import com.ajit.demoproj.ui.HomeViewModel
import com.ajit.demoproj.ui.datasource.NetworkState
import com.ajit.demoproj.ui.datasource.Status
import com.storiyoh.demoproj.repository.FakeRepository
import com.storiyoh.demoproj.repository.FakeUsersDataSource
import com.storiyoh.demoproj.repository.FakeUsersDataSourceFactory
import io.reactivex.disposables.CompositeDisposable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock

@RunWith(JUnit4::class)
class HomeFragTest {

    //Subject unser testee
    private lateinit var homeViewModel: HomeViewModel

    // fake repository to be injected into viewmodel
    private lateinit var repository: FakeRepository

    private val pageSize = 15
    lateinit var list: LiveData<PagedList<Row>>


    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    var usersDataSource: FakeUsersDataSource? = null

    lateinit var sourceFactory: FakeUsersDataSourceFactory

    var row: Row = Row(
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
        repository.addData(row)

        sourceFactory = FakeUsersDataSourceFactory(compositeDisposable, repository)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        list = LivePagedListBuilder<Long, Row>(sourceFactory, config).build()

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

