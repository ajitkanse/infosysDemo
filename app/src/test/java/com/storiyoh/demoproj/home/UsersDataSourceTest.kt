package com.storiyoh.demoproj

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.ItemKeyedDataSource
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource.UsersDataSource
import com.ajit.demoproj.data.api.ApiResp
import com.ajit.demoproj.data.api.Row
import com.ajit.demoproj.data.repository.Repository
import com.ajit.demoproj.ui.datasource.NetworkState
import com.ajit.demoproj.ui.datasource.Status
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.math.sin
import org.mockito.BDDMockito.given



@RunWith(JUnit4::class)
class UsersDataSourceTest {

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository : Repository

    @Mock
    lateinit var compositeDisposable: CompositeDisposable

    var usersDataSource : UsersDataSource? = null

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)

        usersDataSource = UsersDataSource(repository, compositeDisposable)

    }


    @Test
    fun getKeyShouldReturnZero(){

       val s =  usersDataSource?.getKey(Row("","",""))

        assert(s==0L)

    }

    @Test
    fun `should update networkstate initialstate apirespData on method loadInitialTest success`(){

        val observerNetworkState = mock(Observer::class.java) as Observer<NetworkState>
        val observerInitialLoad = mock(Observer::class.java) as Observer<NetworkState>
        val observerApiRespData = mock(Observer::class.java) as Observer<ApiResp>

        usersDataSource?.networkState?.observeForever(observerNetworkState)
        usersDataSource?.initialLoad?.observeForever(observerInitialLoad)
        usersDataSource?.apiRespData?.observeForever(observerApiRespData)

        val loadInitialParams = mock(ItemKeyedDataSource.LoadInitialParams::class.java)
        val loadInitialCallback = mock(ItemKeyedDataSource.LoadInitialCallback::class.java)

        val row1 = Row("desc","image","title")

        val list = listOf<Row>(row1)

        val apiResp = ApiResp(list,"MyTitle")

        val single = Single.just(apiResp)

        Mockito.`when`(repository.getDataFromApi()).thenReturn(
            single
        )

        usersDataSource?.loadInitial(
            loadInitialParams as ItemKeyedDataSource.LoadInitialParams<Long>,
            loadInitialCallback as ItemKeyedDataSource.LoadInitialCallback<Row>
        )

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADING)
        verify(observerInitialLoad, Mockito.times(1)).onChanged(NetworkState.LOADING)

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADED)
        verify(observerInitialLoad, Mockito.times(1)).onChanged(NetworkState.LOADED)

        verify(observerApiRespData).onChanged(apiResp)

        verify(observerNetworkState, never()).onChanged(NetworkState.error(ArgumentMatchers.anyString()))
        verify(observerInitialLoad, never()).onChanged(NetworkState.error(ArgumentMatchers.anyString()))


    }
    @Test
    fun `when repository returns error, on loadInitial() function call, update network state with error`(){

        val observerNetworkState = mock(Observer::class.java) as Observer<NetworkState>
        val observerInitialLoad = mock(Observer::class.java) as Observer<NetworkState>
        val observerApiRespData = mock(Observer::class.java) as Observer<ApiResp>

        usersDataSource?.networkState?.observeForever(observerNetworkState)
        usersDataSource?.initialLoad?.observeForever(observerInitialLoad)
        usersDataSource?.apiRespData?.observeForever(observerApiRespData)

        val loadInitialParams = mock(ItemKeyedDataSource.LoadInitialParams::class.java)
        val loadInitialCallback = mock(ItemKeyedDataSource.LoadInitialCallback::class.java)


        Mockito.`when`(repository.getDataFromApi()).thenReturn(
            Single.error(Throwable("Error"))
        )

        usersDataSource?.loadInitial(
            loadInitialParams as ItemKeyedDataSource.LoadInitialParams<Long>,
            loadInitialCallback as ItemKeyedDataSource.LoadInitialCallback<Row>
        )

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADING)
        verify(observerInitialLoad, Mockito.times(1)).onChanged(NetworkState.LOADING)

        verify(observerNetworkState, never()).onChanged(NetworkState.LOADED)
        verify(observerInitialLoad, never()).onChanged(NetworkState.LOADED)

        val error = NetworkState.error("throwable.message")

        verify(observerNetworkState, Mockito.times(1)).onChanged(error)

        verify(observerInitialLoad, times(1)).onChanged(error)

    }


    @Test
    fun `loadAfterTest`(){

        val observerNetworkState = mock(Observer::class.java) as Observer<NetworkState>

        usersDataSource?.networkState?.observeForever(observerNetworkState)

        val params = mock(ItemKeyedDataSource.LoadParams::class.java)
        val callback = mock(ItemKeyedDataSource.LoadCallback::class.java)

        val row1 = Row("desc","image","title")

        val list = listOf<Row>(row1)

        val apiResp = ApiResp(list,"MyTitle")

        val single = Single.just(apiResp)

        Mockito.`when`(repository.getDataFromApi()).thenReturn(

            single
        )

        usersDataSource?.loadAfter(
            params as ItemKeyedDataSource.LoadParams<Long>,
            callback as ItemKeyedDataSource.LoadCallback<Row>
        )

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADING)

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADED)

        verify(observerNetworkState, never()).onChanged(NetworkState.error(ArgumentMatchers.anyString()))

    }

    @Test
    fun `when repository returns error, on loadAfter() function call, update network state with error`(){

        val observerNetworkState = mock(Observer::class.java) as Observer<NetworkState>
        val observerInitialLoad = mock(Observer::class.java) as Observer<NetworkState>

        usersDataSource?.networkState?.observeForever(observerNetworkState)
        usersDataSource?.initialLoad?.observeForever(observerInitialLoad)

        val params = mock(ItemKeyedDataSource.LoadParams::class.java)
        val callback = mock(ItemKeyedDataSource.LoadCallback::class.java)

        Mockito.`when`(repository.getDataFromApi()).thenReturn(
            Single.error(Throwable("Error"))
        )

        usersDataSource?.loadAfter(
            params as ItemKeyedDataSource.LoadParams<Long>,
            callback as ItemKeyedDataSource.LoadCallback<Row>
        )

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADING)

        verify(observerNetworkState, never()).onChanged(NetworkState.LOADED)

        val error = NetworkState.error("throwable.message")

        verify(observerNetworkState, Mockito.times(1)).onChanged(error)

    }

    @Test
    fun `setRetryTest`(){

        usersDataSource?.retry()

    }


    @After
    fun tearDown() {

    }
}