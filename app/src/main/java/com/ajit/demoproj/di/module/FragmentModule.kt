package com.ajit.demoproj.di.module

import com.ajit.demoproj.ui.HomeViewModel
import com.ajit.demoproj.data.repository.Repository
import com.ajit.demoproj.util.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class FragmentModule {

    @Provides
    fun provideViewModel(repository: Repository,
                         schedulerProvider: SchedulerProvider
    ) = HomeViewModel(repository, schedulerProvider)
}