package com.st10445368.gitbbc.data


import com.st10445368.gitbbc.models.NewsSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WorldNewsApiService {

    @GET("search-news")
    suspend fun searchNews(
        @Query("source-countries") sourceCountries: String? = null,
        @Query("categories") category: String? = null,
        @Query("text") textQuery: String? = null,
        @Query("language") language: String = "en",
        @Query("earliest-publish-date") earliestPublishDate: String? = null,
        @Query("number") number: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("api-key") apiKey: String
    ): Response<NewsSearchResponse>
}