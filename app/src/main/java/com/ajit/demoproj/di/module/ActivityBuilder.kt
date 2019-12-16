package com.ajit.demoproj.di.module

import com.ajit.demoproj.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = arrayOf(ActivityModule::class))
    abstract fun bindMainActivity(): MainActivity


}