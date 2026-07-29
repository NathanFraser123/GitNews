package com.st10445368.gitbbc.ui.state



import com.st10445368.gitbbc.data.repository.ErrorType
import com.st10445368.gitbbc.models.NewsArticle

/**
 * State snapshot of the News screen.
 */
data class NewsUiState(
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isEndReached: Boolean = false,
    val totalResults: Int = 0,
    val currentOffset: Int = 0,
    val selectedCountryCode: String = "za", // Default to South Africa
    val selectedCountryName: String = "South Africa",
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val isLocationLoading: Boolean = false,
    val errorMessage: String? = null,
    val errorType: ErrorType? = null
)