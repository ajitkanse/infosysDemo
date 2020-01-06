package com.storiyoh.demoproj.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.ajit.demoproj.data.api.ApiResp
import com.ajit.demoproj.data.api.Row
import io.reactivex.Single
import javax.inject.Singleton

@Singleton
class FakeRepository {

    var list: ArrayList<Row> = ArrayList()

    fun getDataFromApi(): Single<ApiResp> {

        var resp : ApiResp = ApiResp(list,"About Canada")

        val respSing: Single<ApiResp> = Single.just(resp)


        return respSing

    }

    fun addData(row: Row) {
        for (i in 1..5) {
            list.add(
                Row(
                    title = "${row.title}  $i ",
                    description = "${row.description}  $i ",
                    imageHref = "${row.imageHref}  $i "
                )
            )
        }
    }
}