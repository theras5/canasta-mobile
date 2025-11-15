package com.example.canasta.ui.components.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enum de avatares predefinidos con sus colores e iconos
 */
enum class AvatarOption(val color: Color, val icon: ImageVector) {
    GREEN_DARK(Color(0xFF4A5C3A), Icons.Default.Face),
    ORANGE(Color(0xFFFF7043), Icons.Default.Pets),
    BLUE(Color(0xFF42A5F5), Icons.Default.Star),
    PURPLE(Color(0xFF9C27B0), Icons.Default.Favorite),
    GREEN_LIGHT(Color(0xFF66BB6A), Icons.Default.EmojiNature),
    RED(Color(0xFFE53935), Icons.Default.LocalFlorist);

    companion object {
        @Suppress("unused")
        fun fromOrdinal(ordinal: Int): AvatarOption {
            return entries.getOrNull(ordinal) ?: GREEN_DARK
        }
    }
}

