package com.storiyoh.demoproj

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.ajit.demoproj.data.local.Post
import com.ajit.demoproj.data.local.PostDao
import com.ajit.demoproj.data.local.PostDb
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@RunWith(AndroidJUnit4::class)
class PostDBTest {
    @JvmField @Rule
    var countingTaskExecutorRule = CountingTaskExecutorRule()

    private var postDAO: PostDao? = null
    private var db: PostDb? = null

    private val mLifecycleOwner = TestLifecycleOwner()

    @Before
    fun onCreateDB() {

        InstrumentationRegistry.getInstrumentation().runOnMainSync { mLifecycleOwner.handleEvent(
            Lifecycle.Event.ON_START) }

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PostDb::class.java
        ).build()

        postDAO = db!!.postDao()

    }


    @Test
    fun `should_Insert_One_Post`() {

        val config = PagedList.Config.Builder()
            .setPageSize(1)
            .setInitialLoadSizeHint(3)
            .setPrefetchDistance(2)
            .setEnablePlaceholders(false)
            .build()

        val pagedList = LivePagedListBuilder(postDAO!!.getAllRowsPagedList(), config).build()

        observeForever(pagedList)

        drain()

        postDAO!!.insertPost(Post("India", "waertghj","asdfg"))
        drain()

        MatcherAssert.assertThat(pagedList.getValue()!!.size, CoreMatchers.`is`(1))

        val post1 = Post("Canada2","ABCd","image")
        val post2 = Post("Canada3","ABCe","image")
        val post3 = Post("Canada4","ABCf","image")

        val list = arrayListOf<Post>(post1,post2,post3)

        postDAO!!.insertAllRow(list)


        drain()

        MatcherAssert.assertThat(pagedList.getValue()!!.size, CoreMatchers.`is`(3))

        pagedList.getValue()!!.loadAround(2)

        drain()
        MatcherAssert.assertThat(pagedList.getValue()!!.size, CoreMatchers.`is`(4))

    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db!!.close()
    }


    private fun drain() {
        try {
            countingTaskExecutorRule.drainTasks(30, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            throw AssertionError("interrupted", e)
        } catch (e: TimeoutException) {
            throw AssertionError("drain timed out", e)
        }

    }

    private fun <T> observeForever(liveData: LiveData<T>) {

        val futureTask =  UnitTestUtils.observeForever(liveData,mLifecycleOwner)

        ArchTaskExecutor.getMainThreadExecutor().execute(futureTask)
        try {
            futureTask.get()
        } catch (e: InterruptedException) {
            throw AssertionError("interrupted", e)
        } catch (e: ExecutionException) {
            throw AssertionError("execution error", e)
        }

    }

}