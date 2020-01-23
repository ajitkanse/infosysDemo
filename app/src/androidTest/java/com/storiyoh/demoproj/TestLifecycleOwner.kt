package com.storiyoh.demoproj

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class TestLifecycleOwner() : LifecycleOwner {

    var mLifecycle : LifecycleRegistry

    init {
        mLifecycle = LifecycleRegistry(this)
    }

    override fun getLifecycle()  : Lifecycle {

        return mLifecycle;
    }

    fun handleEvent(event : Lifecycle.Event) {

        mLifecycle.handleLifecycleEvent(event)
    }
}