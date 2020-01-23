package com.ajit.demoproj.ui

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajit.demoproj.base.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home_fragment.*
import javax.inject.Inject
import androidx.paging.PagedList
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.adapter.PostAdapter
import com.ajit.demoproj.R
import com.ajit.demoproj.data.api.ApiPostResp
import com.ajit.demoproj.data.local.Post
import com.ajit.demoproj.data.datasource.NetworkState
import com.ajit.demoproj.data.datasource.Status
import com.ajit.demoproj.util.Constants.Companion.LIST_STATE_KEY
import com.ajit.demoproj.viewmodel.HomeViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.item_network_state.*


class HomeFragment : BaseFragment() {

    private lateinit var mPostListState: Parcelable
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var homeViewModel: HomeViewModel

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initAdapter()
        initOnservers()
        initSwipeToRefresh()

    }

    private fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        postAdapter = PostAdapter {
            homeViewModel.retry()
        }
        postRecyclerView.layoutManager = linearLayoutManager
        postRecyclerView.setHasFixedSize(true)
        postRecyclerView.adapter = postAdapter

    }

    private fun initOnservers() {
        homeViewModel.postList.observe(this, androidx.lifecycle.Observer<PagedList<Post>> {

        })

        homeViewModel.getNetworkData().observe(this, androidx.lifecycle.Observer<ApiPostResp> {
            (activity as DaggerAppCompatActivity).supportActionBar?.title = it.title
        })

        homeViewModel.getNetworkState().observe(this,
            androidx.lifecycle.Observer<NetworkState> { postAdapter.setNetworkState(it) })

        homeViewModel.getPersistentData().observe(this, Observer<PagedList<Post>> {
            postAdapter.submitList(it)
        })

        setInitialLoadingState(NetworkState.LOADING)

    }

    private fun initSwipeToRefresh() {
        homeViewModel.getRefreshState().observe(this, Observer { networkState ->
            if (postAdapter.currentList != null) {
                if (postAdapter.currentList!!.size > 0) {
                    postSwipeRefreshLayout.isRefreshing =
                        networkState?.status == NetworkState.LOADING.status
                } else {
                    setInitialLoadingState(networkState)
                }
            } else {
                setInitialLoadingState(networkState)
            }
        })
        postSwipeRefreshLayout.setOnRefreshListener({ homeViewModel.refresh() })
    }

    private fun setInitialLoadingState(networkState: NetworkState?) {
        //error message
        errorMessageTextView.visibility =
            if (networkState?.message != null) View.VISIBLE else View.GONE
        if (networkState?.message != null) {
            errorMessageTextView.text = networkState.message
        }

        //loading and retry
        retryLoadingButton.visibility =
            if (networkState?.status == Status.FAILED) View.VISIBLE else View.GONE
        loadingProgressBar.visibility =
            if (networkState?.status == Status.RUNNING) View.VISIBLE else View.GONE

        postSwipeRefreshLayout.isEnabled = networkState?.status == Status.SUCCESS
        retryLoadingButton.setOnClickListener { homeViewModel.retry() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mPostListState = linearLayoutManager.onSaveInstanceState()!!
        outState.putParcelable(LIST_STATE_KEY, mPostListState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            mPostListState = savedInstanceState.getParcelable(LIST_STATE_KEY)!!
            linearLayoutManager.onRestoreInstanceState(mPostListState)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}


