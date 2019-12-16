package com.ajit.demoproj.di.module

import com.ajit.demoproj.ui.MainActivityViewModel
import com.ajit.demoproj.data.repository.Repository
import com.ajit.demoproj.util.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {

    @Provides
    fun provideViewModel(repository: Repository,
                         schedulerProvider: SchedulerProvider
    )
            = MainActivityViewModel(repository, schedulerProvider)
}