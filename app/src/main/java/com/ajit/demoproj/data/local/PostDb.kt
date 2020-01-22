package com.ajit.demoproj.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Post::class], version = 1,exportSchema = false)
abstract class PostDb : RoomDatabase() {

    abstract fun postDao(): PostDao

}
