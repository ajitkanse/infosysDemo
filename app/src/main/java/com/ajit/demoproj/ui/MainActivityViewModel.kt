package com.ajit.demoproj.ui

import androidx.lifecycle.ViewModel
import com.ajit.demoproj.data.api.ApiResp
import com.ajit.demoproj.data.repository.Repository
import com.ajit.demoproj.util.SchedulerProvider
import io.reactivex.Single


class MainActivityViewModel(private val repository: Repository,
                            private val schedulerProvider: SchedulerProvider
) : ViewModel(){

}