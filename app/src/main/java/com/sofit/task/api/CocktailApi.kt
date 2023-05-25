package com.sofit.task.api

import com.sofit.task.db.model.CocktailResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApi {
    @GET("search.php")
    suspend fun getCocktails(@Query("s") searchQuery: String): CocktailResponse
    @GET("lookup.php")
    suspend fun getCocktailById(@Query("i") searchQuery: Int?): CocktailResponse

}