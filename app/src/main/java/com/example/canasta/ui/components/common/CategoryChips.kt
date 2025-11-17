package com.example.canasta.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.R
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.ui.theme.CanastaTheme
import com.example.canasta.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChips(
    categories: List<String>,
    selectedCategory: String? = null,
    onCategorySelected: (String?) -> Unit = {}
) {
    val allCategoriesText = stringResource(R.string.all_categories)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category || (selectedCategory == null && category == allCategoriesText)
            FilterChip(
                selected = isSelected,
                onClick = {
                    onCategorySelected(if (category == allCategoriesText) null else category)
                },
                label = { Text(category) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon"
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Secondary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChipsApi(
    categories: List<GetCategory>,
    selectedCategory: GetCategory?,
    onCategorySelected: (GetCategory?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(listOf<GetCategory?>(null) + categories) { category ->
            val isSelected = (selectedCategory == null && category == null) ||
                    (selectedCategory != null && category != null && selectedCategory.id == category.id)
            val label = category?.name ?: stringResource(R.string.all_categories)
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(label) },
                leadingIcon = if (isSelected) {
                    { Icon(imageVector = Icons.Filled.Done, contentDescription = null) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Secondary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryChipsPreview() {
    CanastaTheme {
        CategoryChips(listOf("Lácteos", "Carnes", "Verduras", "Frutas", "Panadería"))
    }
}