package com.ahmedabdelmeged.pagingwithrxjava.kotlin.data.datasource


import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.ajit.demoproj.data.api.ApiPostResp
import com.ajit.demoproj.data.local.Post
import com.ajit.demoproj.data.repository.PostRepository
import com.ajit.demoproj.data.datasource.NetworkState
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

class PostsDataSource(
    private val postRepository: PostRepository,
    private val compositeDisposable: CompositeDisposable
) : ItemKeyedDataSource<Long, Post>() {

    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()
    val apiRespData = MutableLiveData<ApiPostResp>()

    private var retryCompletable: Completable? = null

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ }, { throwable -> /*Timber.e(throwable.message)*/ })
            )
        }
    }

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Post>) {

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        compositeDisposable.add(postRepository.getPostDataFromApi().subscribe({ respData ->
            setRetry(null)
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)

            apiRespData.postValue(respData)
            callback.onResult(respData.rows)

            postRepository.updateUser(respData.rows)

        }, { throwable ->
            setRetry(Action { loadInitial(params, callback) })
            val error = NetworkState.error("Internal Server problem")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }))
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Post>) {

        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(postRepository.getPostDataFromApi().subscribe({ respData ->
            setRetry(null)
            networkState.postValue(NetworkState.LOADED)

            callback.onResult(respData.rows)
            postRepository.updateUser(respData.rows)

        }, { throwable ->
            setRetry(Action { loadAfter(params, callback) })
            // publish the error
            networkState.postValue(NetworkState.error("Internal Server problem"))
        }))
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Post>) {
        // ignored, since we only ever append to our initial load
    }

    override fun getKey(item: Post): Long {
        return 0
    }

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

}