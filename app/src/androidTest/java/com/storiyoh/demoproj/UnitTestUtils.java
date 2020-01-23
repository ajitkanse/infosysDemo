package com.storiyoh.demoproj;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class UnitTestUtils {

     static <T> FutureTask observeForever(final LiveData<T> liveData, TestLifecycleOwner mLifecycleOwner) {

        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            liveData.observe(mLifecycleOwner, t -> {
            });
            return null;
        });
        ArchTaskExecutor.getMainThreadExecutor().execute(futureTask);
        try {
            futureTask.get();
        } catch (InterruptedException e) {
            throw new AssertionError("interrupted", e);
        } catch (ExecutionException e) {
            throw new AssertionError("execution error", e);
        }

       return futureTask;
    }

}
