package com.st10445368.gitbbc.models

import com.google.gson.annotations.SerializedName

/**
 * World News API Response wrapper.
 */
data class NewsSearchResponse(
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("number") val number: Int = 0,
    @SerializedName("available") val available: Int = 0,
    @SerializedName("news") val news: List<NewsArticle> = emptyList()
)

/**
 * Individual News Article object.
 * Optional fields default to null or empty lists to prevent parsing crashes on incomplete data.
 */
data class NewsArticle(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("video") val video: String? = null,
    @SerializedName("publish_date") val publishDate: String? = null,
    @SerializedName("authors") val authors: List<String>? = emptyList(),
    @SerializedName("category") val category: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("source_country") val sourceCountry: String? = null,
    @SerializedName("sentiment") val sentiment: Double? = null
)