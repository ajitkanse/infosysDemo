package com.ajit.demoproj.data.repository

import com.ajit.demoproj.data.api.ApiResp
import com.ajit.demoproj.data.api.ApiService
import com.google.gson.Gson
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class Repository @Inject constructor(public val apiService: ApiService) {

    fun getDataFromApi(): Single<ApiResp> = apiService.getDataFromApi()

}