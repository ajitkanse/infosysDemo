package com.storiyoh.demoproj

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.ItemKeyedDataSource
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource.PostsDataSource
import com.ajit.demoproj.data.api.ApiPostResp
import com.ajit.demoproj.data.local.Post
import com.ajit.demoproj.data.repository.PostRepository
import com.ajit.demoproj.data.datasource.NetworkState
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



@RunWith(JUnit4::class)
class PostsDataSourceTest {

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var postRepository : PostRepository

    @Mock
    lateinit var compositeDisposable: CompositeDisposable

    var postsDataSource : PostsDataSource? = null

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)

        postsDataSource = PostsDataSource(postRepository, compositeDisposable)

    }


    @Test
    fun getKeyShouldReturnZero(){

       val s =  postsDataSource?.getKey(Post("","",""))

        assert(s==0L)

    }

    @Test
    fun `should update networkstate initialstate apirespData on method loadInitialTest success`(){

        val observerNetworkState = mock(Observer::class.java) as Observer<NetworkState>
        val observerInitialLoad = mock(Observer::class.java) as Observer<NetworkState>
        val observerApiRespData = mock(Observer::class.java) as Observer<ApiPostResp>

        postsDataSource?.networkState?.observeForever(observerNetworkState)
        postsDataSource?.initialLoad?.observeForever(observerInitialLoad)
        postsDataSource?.apiRespData?.observeForever(observerApiRespData)

        val loadInitialParams = mock(ItemKeyedDataSource.LoadInitialParams::class.java)
        val loadInitialCallback = mock(ItemKeyedDataSource.LoadInitialCallback::class.java)

        val row1 = Post("title","description","imageHref")

        val list = listOf<Post>(row1)

        val apiResp = ApiPostResp(list,"MyTitle")

        val single = Single.just(apiResp)

        Mockito.`when`(postRepository.getPostDataFromApi()).thenReturn(
            single
        )

        postsDataSource?.loadInitial(
            loadInitialParams as ItemKeyedDataSource.LoadInitialParams<Long>,
            loadInitialCallback as ItemKeyedDataSource.LoadInitialCallback<Post>
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
        val observerApiRespData = mock(Observer::class.java) as Observer<ApiPostResp>

        postsDataSource?.networkState?.observeForever(observerNetworkState)
        postsDataSource?.initialLoad?.observeForever(observerInitialLoad)
        postsDataSource?.apiRespData?.observeForever(observerApiRespData)

        val loadInitialParams = mock(ItemKeyedDataSource.LoadInitialParams::class.java)
        val loadInitialCallback = mock(ItemKeyedDataSource.LoadInitialCallback::class.java)


        Mockito.`when`(postRepository.getPostDataFromApi()).thenReturn(
            Single.error(Throwable("Error"))
        )

        postsDataSource?.loadInitial(
            loadInitialParams as ItemKeyedDataSource.LoadInitialParams<Long>,
            loadInitialCallback as ItemKeyedDataSource.LoadInitialCallback<Post>
        )

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADING)
        verify(observerInitialLoad, Mockito.times(1)).onChanged(NetworkState.LOADING)

        verify(observerNetworkState, never()).onChanged(NetworkState.LOADED)
        verify(observerInitialLoad, never()).onChanged(NetworkState.LOADED)

        val error = NetworkState.error("Internal Server problem")

        verify(observerNetworkState, Mockito.times(1)).onChanged(error)

        verify(observerInitialLoad, times(1)).onChanged(error)

    }


    @Test
    fun `loadAfterTest`(){

        val observerNetworkState = mock(Observer::class.java) as Observer<NetworkState>

        postsDataSource?.networkState?.observeForever(observerNetworkState)

        val params = mock(ItemKeyedDataSource.LoadParams::class.java)
        val callback = mock(ItemKeyedDataSource.LoadCallback::class.java)

        val row1 = Post("title","description","imageHref")


        val list = listOf<Post>(row1)

        val apiResp = ApiPostResp(list,"MyTitle")

        val single = Single.just(apiResp)

        Mockito.`when`(postRepository.getPostDataFromApi()).thenReturn(

            single
        )

        postsDataSource?.loadAfter(
            params as ItemKeyedDataSource.LoadParams<Long>,
            callback as ItemKeyedDataSource.LoadCallback<Post>
        )

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADING)

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADED)

        verify(observerNetworkState, never()).onChanged(NetworkState.error(ArgumentMatchers.anyString()))

    }

    @Test
    fun `when repository returns error, on loadAfter() function call, update network state with error`(){

        val observerNetworkState = mock(Observer::class.java) as Observer<NetworkState>
        val observerInitialLoad = mock(Observer::class.java) as Observer<NetworkState>

        postsDataSource?.networkState?.observeForever(observerNetworkState)
        postsDataSource?.initialLoad?.observeForever(observerInitialLoad)

        val params = mock(ItemKeyedDataSource.LoadParams::class.java)
        val callback = mock(ItemKeyedDataSource.LoadCallback::class.java)

        Mockito.`when`(postRepository.getPostDataFromApi()).thenReturn(
            Single.error(Throwable("Error"))
        )

        postsDataSource?.loadAfter(
            params as ItemKeyedDataSource.LoadParams<Long>,
            callback as ItemKeyedDataSource.LoadCallback<Post>
        )

        verify(observerNetworkState, Mockito.times(1)).onChanged(NetworkState.LOADING)

        verify(observerNetworkState, never()).onChanged(NetworkState.LOADED)

        val error = NetworkState.error("Internal Server problem")

        verify(observerNetworkState, Mockito.times(1)).onChanged(error)

    }

    @Test
    fun `setRetryTest`(){

        postsDataSource?.retry()

    }


    @After
    fun tearDown() {

    }
}