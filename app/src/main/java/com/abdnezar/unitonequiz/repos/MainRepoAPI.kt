package com.abdnezar.unitonequiz.repos

import com.abdnezar.unitonequiz.data.models.Home
import com.abdnezar.unitonequiz.utils.MyApp.Companion.HOME_URL
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MainRepoAPI {
    @GET(HOME_URL)
    suspend fun getHomeData() : Response<Home>
}