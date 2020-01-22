package com.ajit.demoproj.viewmodel

import androidx.lifecycle.ViewModel
import com.ajit.demoproj.data.repository.PostRepository
import com.ajit.demoproj.util.SchedulerProvider


class MainActivityViewModel(private val postRepository: PostRepository,
                            private val schedulerProvider: SchedulerProvider
) : ViewModel()