package com.st10445368.gitbbc.data.repository

import com.st10445368.gitbbc.models.NewsArticle


sealed class NewsResult {
    data class Success(
        val articles: List<NewsArticle>,
        val totalAvailable: Int,
        val isEndReached: Boolean
    ) : NewsResult()

    data class Error(val message: String, val type: ErrorType) : NewsResult()
}

enum class ErrorType {
    MISSING_API_KEY,
    INVALID_API_KEY,
    QUOTA_EXCEEDED,
    NO_INTERNET,
    SERVER_ERROR,
    EMPTY_RESULTS,
    UNKNOWN
}