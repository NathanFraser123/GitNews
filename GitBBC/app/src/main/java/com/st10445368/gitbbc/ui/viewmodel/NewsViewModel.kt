package com.st10445368.gitbbc.ui.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st10445368.gitbbc.data.repository.NewsRepository
import com.st10445368.gitbbc.data.repository.NewsResult
import com.st10445368.gitbbc.location.CountryGeocoder
import com.st10445368.gitbbc.location.LocationHelper
import com.st10445368.gitbbc.models.CountryData
import com.st10445368.gitbbc.ui.state.NewsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository = NewsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    init {
        // Initial fetch using default country (South Africa - za)
        fetchNews(isRefresh = true)
    }

    /**
     * Fetches news articles for the active country, category, and pagination offset.
     */
    fun fetchNews(isRefresh: Boolean = false) {
        val currentState = _uiState.value

        if (isRefresh) {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    errorType = null,
                    currentOffset = 0,
                    articles = emptyList()
                )
            }
        } else {
            if (currentState.isLoadingMore || currentState.isEndReached) return
            _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }
        }

        val targetOffset = if (isRefresh) 0 else currentState.currentOffset

        viewModelScope.launch {
            val result = repository.getNews(
                countryCode = _uiState.value.selectedCountryCode,
                category = _uiState.value.selectedCategory,
                queryText = _uiState.value.searchQuery,
                offset = targetOffset,
                pageSize = pageSize
            )

            when (result) {
                is NewsResult.Success -> {
                    _uiState.update { state ->
                        val updatedList = if (isRefresh) result.articles else state.articles + result.articles
                        state.copy(
                            articles = updatedList,
                            isLoading = false,
                            isLoadingMore = false,
                            isEndReached = result.isEndReached,
                            totalResults = result.totalAvailable,
                            currentOffset = targetOffset + result.articles.size,
                            errorMessage = null,
                            errorType = null
                        )
                    }
                }
                is NewsResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = result.message,
                            errorType = result.type
                        )
                    }
                }
            }
        }
    }

    /**
     * Updates selected category and re-fetches from offset 0.
     */
    fun selectCategory(category: String?) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.update { it.copy(selectedCategory = category) }
        fetchNews(isRefresh = true)
    }

    /**
     * Updates selected country manually and re-fetches news.
     */
    fun selectCountry(countryCode: String) {
        if (_uiState.value.selectedCountryCode == countryCode) return
        val countryName = CountryData.getCountryName(countryCode)
        _uiState.update {
            it.copy(
                selectedCountryCode = countryCode.lowercase(),
                selectedCountryName = countryName
            )
        }
        fetchNews(isRefresh = true)
    }

    /**
     * Triggers GPS location lock, performs reverse geocoding, and loads local news.
     */
    fun fetchLocationAndNews(context: Context) {
        _uiState.update { it.copy(isLocationLoading = true) }

        viewModelScope.launch {
            val locationHelper = LocationHelper(context)
            val geocoder = CountryGeocoder(context)

            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                val geoResult = geocoder.getCountryFromCoordinates(location.latitude, location.longitude)
                if (geoResult != null) {
                    _uiState.update { state ->
                        state.copy(
                            userLatitude = geoResult.latitude,
                            userLongitude = geoResult.longitude,
                            selectedCountryCode = geoResult.countryCode,
                            selectedCountryName = geoResult.countryName,
                            isLocationLoading = false
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            userLatitude = location.latitude,
                            userLongitude = location.longitude,
                            isLocationLoading = false
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isLocationLoading = false) }
            }

            // Refresh news with updated country code
            fetchNews(isRefresh = true)
        }
    }

    /**
     * Updates search query keyword.
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Executes text search.
     */
    fun performSearch() {
        fetchNews(isRefresh = true)
    }
}