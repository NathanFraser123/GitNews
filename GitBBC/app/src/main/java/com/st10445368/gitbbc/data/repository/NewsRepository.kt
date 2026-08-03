package com.st10445368.gitbbc.data.repository

import com.st10445368.gitbbc.BuildConfig
import com.st10445368.gitbbc.data.NetworkModule
import com.st10445368.gitbbc.data.WorldNewsApiService
import com.st10445368.gitbbc.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class NewsRepository(
    // Use lazy initialization so NetworkModule.apiService isn't instantiated on the main thread during constructor call
    private val getApiService: () -> WorldNewsApiService = { NetworkModule.apiService }
) {
    private val apiService by lazy { getApiService() }

    /**
     * Fetches news articles from World News API with pagination support.
     */
    suspend fun getNews(
        countryCode: String? = null,
        category: String? = null,
        queryText: String? = null,
        offset: Int = 0,
        pageSize: Int = 20
    ): NewsResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.WORLD_NEWS_API_KEY

        // Check for missing API Key before making request
        if (apiKey.isBlank()) {
            return@withContext NewsResult.Error(
                message = "API key is missing. Please add WORLD_NEWS_API_KEY to local.properties.",
                type = ErrorType.MISSING_API_KEY
            )
        }

        try {
            // Dynamic date calculation (e.g., 30 days prior to today)
            val earliestDate = DateUtils.getEarliestPublishDate(30)

            val response = apiService.searchNews(
                sourceCountries = countryCode,
                category = category,
                textQuery = queryText.takeIf { !it.isNullOrBlank() },
                language = "en",
                earliestPublishDate = earliestDate,
                number = pageSize,
                offset = offset,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                val body = response.body()
                val articles = body?.news ?: emptyList()
                val available = body?.available ?: 0
                val isEndReached = offset + articles.size >= available || articles.isEmpty()

                if (articles.isEmpty() && offset == 0) {
                    NewsResult.Error(
                        message = "No articles found matching your criteria.",
                        type = ErrorType.EMPTY_RESULTS
                    )
                } else {
                    NewsResult.Success(
                        articles = articles,
                        totalAvailable = available,
                        isEndReached = isEndReached
                    )
                }
            } else {
                when (response.code()) {
                    401 -> NewsResult.Error("Unauthorized: Invalid API key.", ErrorType.INVALID_API_KEY)
                    402, 429 -> NewsResult.Error("API quota or rate limit exceeded.", ErrorType.QUOTA_EXCEEDED)
                    else -> NewsResult.Error("Server error (${response.code()}): ${response.message()}", ErrorType.SERVER_ERROR)
                }
            }
        } catch (e: IOException) {
            NewsResult.Error(
                message = "No internet connection or network timeout.",
                type = ErrorType.NO_INTERNET
            )
        } catch (e: HttpException) {
            NewsResult.Error(
                message = "HTTP Exception: ${e.message()}",
                type = ErrorType.SERVER_ERROR
            )
        } catch (e: Exception) {
            NewsResult.Error(
                message = "An unexpected error occurred: ${e.localizedMessage}",
                type = ErrorType.UNKNOWN
            )
        }
    }
}