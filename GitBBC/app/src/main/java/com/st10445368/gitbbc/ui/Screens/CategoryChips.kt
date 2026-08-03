package com.st10445368.gitbbc.ui.Screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val categoriesList = listOf(
    "all" to "All",
    "business" to "Business",
    "technology" to "Technology",
    "sports" to "Sports",
    "science" to "Science",
    "entertainment" to "Entertainment",
    "health" to "Health",
    "politics" to "Politics"
)

@Composable
fun CategoryChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categoriesList) { (key, label) ->
            val isSelected = if (key == "all") selectedCategory == null else selectedCategory == key
            FilterChip(
                selected = isSelected,
                onClick = {
                    onCategorySelected(if (key == "all") null else key)
                },
                label = { Text(label) }
            )
        }
    }
}