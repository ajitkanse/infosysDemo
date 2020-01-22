package com.storiyoh.demoproj.repository

import com.ajit.demoproj.data.api.ApiPostResp
import com.ajit.demoproj.data.local.Post
import io.reactivex.Single
import javax.inject.Singleton

@Singleton
class FakeRepository {

    var postList: ArrayList<Post> = ArrayList()

    fun getDataFromApi(): Single<ApiPostResp> {

        var postResp = ApiPostResp(postList,"About Canada")

        val postRespSing: Single<ApiPostResp> = Single.just(postResp)

        return postRespSing

    }

    fun addData(post: Post) {
        for (i in 1..5) {
            postList.add(
                Post(
                    title = "${post.title}  $i ",
                    description = "${post.description}  $i ",
                    imageHref = "${post.imageHref}  $i "
                )
            )
        }
    }
}