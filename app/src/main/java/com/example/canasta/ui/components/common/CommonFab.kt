package com.example.canasta.ui.components.common

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.canasta.ui.theme.Secondary

/**
 * FAB redondo reutilizable con icono y onClick customizables
 */
@Composable
fun CommonFab(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    containerColor: Color = Secondary,
    contentColor: Color = Color.White
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = CircleShape
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

