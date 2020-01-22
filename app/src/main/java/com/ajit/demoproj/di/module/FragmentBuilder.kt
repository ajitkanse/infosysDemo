package com.ajit.demoproj.di.module

import com.ajit.demoproj.ui.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {
    @ContributesAndroidInjector(modules = arrayOf(FragmentModule::class))
    abstract fun bindHomeFragment(): HomeFragment
}