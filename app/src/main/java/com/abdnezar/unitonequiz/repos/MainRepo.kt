package com.abdnezar.unitonequiz.repos

import com.abdnezar.unitonequiz.data.APIClient
import com.abdnezar.unitonequiz.data.models.Home
import retrofit2.Call
import retrofit2.Response

class MainRepo {
    suspend fun getHomeData(): Response<Home> {
        return APIClient().getApiInterface().getHomeData()
    }
}