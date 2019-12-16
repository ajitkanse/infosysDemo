package com.ajit.demoproj.di.component

import android.app.Application
import com.ajit.demoproj.base.ApplicationBase
import com.ajit.demoproj.di.module.ActivityBuilder
import com.ajit.demoproj.di.module.AppModule
import com.ajit.demoproj.di.module.FragmentBuilder
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(
    AndroidInjectionModule::class, AppModule::class,
    ActivityBuilder::class,FragmentBuilder::class))

interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }

    fun inject(app: ApplicationBase)
}