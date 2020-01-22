package com.ajit.demoproj.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "post_table")
data class Post(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageHref") val imageHref: String
)