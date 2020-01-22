package com.ajit.demoproj.di.module

import com.ajit.demoproj.viewmodel.MainActivityViewModel
import com.ajit.demoproj.data.repository.PostRepository
import com.ajit.demoproj.util.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {

    @Provides
    fun provideViewModel(
        postRepository: PostRepository,
        schedulerProvider: SchedulerProvider
    ) = MainActivityViewModel(postRepository, schedulerProvider)
}