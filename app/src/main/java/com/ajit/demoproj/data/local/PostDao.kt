package com.ajit.demoproj.data.local

import androidx.room.*

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllRow(posts: List<Post>)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPost(posts: Post)


    @Query("SELECT * FROM post_table")
    fun getAllRowsPagedList(): androidx.paging.DataSource.Factory<Int, Post>

}