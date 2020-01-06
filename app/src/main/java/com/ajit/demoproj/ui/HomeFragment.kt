package com.ajit.demoproj.ui

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajit.demoproj.base.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home_fragment.*
import javax.inject.Inject
import androidx.paging.PagedList
import com.ahmedabdelmeged.pagingwithrxjava.kotlin.adapter.UserAdapter
import com.ajit.demoproj.R
import com.ajit.demoproj.data.api.ApiResp
import com.ajit.demoproj.data.api.Row
import com.ajit.demoproj.ui.datasource.NetworkState
import com.ajit.demoproj.ui.datasource.Status
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.item_network_state.*


class HomeFragment : BaseFragment() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var viewModel: HomeViewModel

    lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initAdapter()
        initSwipeToRefresh()

    }

    private fun initAdapter() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        userAdapter = UserAdapter {
            viewModel.retry()
        }
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = userAdapter


        viewModel.list.observe(this, androidx.lifecycle.Observer<PagedList<Row>> {

            userAdapter.submitList(it)
        }
        )

        viewModel.getNetworkData().observe(this, androidx.lifecycle.Observer<ApiResp> {
            (activity as DaggerAppCompatActivity).supportActionBar?.title = it.title
        }
        )
        viewModel.getNetworkState().observe(
            this,
            androidx.lifecycle.Observer<NetworkState> { userAdapter.setNetworkState(it) })


    }

    /**
     * Init swipe to refresh and enable pull to refresh only when there are items in the adapter
     */
    private fun initSwipeToRefresh() {
        viewModel.getRefreshState().observe(this, Observer { networkState ->
            if (userAdapter.currentList != null) {
                if (userAdapter.currentList!!.size > 0) {
                    usersSwipeRefreshLayout.isRefreshing =
                        networkState?.status == NetworkState.LOADING.status
                } else {
                    setInitialLoadingState(networkState)
                }
            } else {
                setInitialLoadingState(networkState)
            }
        })
        usersSwipeRefreshLayout.setOnRefreshListener({ viewModel.refresh() })
    }

    /**
     * Show the current network state for the first load when the user list
     * in the adapter is empty and disable swipe to scroll at the first loading
     *
     * @param networkState the new network state
     */
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

        usersSwipeRefreshLayout.isEnabled = networkState?.status == Status.SUCCESS
        retryLoadingButton.setOnClickListener { viewModel.retry() }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}


