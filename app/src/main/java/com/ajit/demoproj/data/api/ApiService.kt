package com.ajit.demoproj.data.api
import io.reactivex.Single
import retrofit2.http.*


interface ApiService {

    @Headers("Accept: " + "application/json")
    @POST("s/2iodh4vg0eortkl/facts.json")
    fun getPostDataFromApi(): Single<ApiPostResp>
}