package com.example.canasta.ui.components.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.canasta.ui.theme.Secondary

/**
 * FAB redondo reutilizable con icono y onClick customizables
 * Soporta tanto ImageVector como recursos drawable
 */
@Composable
fun CommonFab(
    icon: ImageVector? = null,
    @DrawableRes iconRes: Int? = null,
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
        when {
            icon != null -> Icon(imageVector = icon, contentDescription = contentDescription, tint = contentColor)
            iconRes != null -> Icon(painter = painterResource(id = iconRes), contentDescription = contentDescription, tint = contentColor)
            else -> throw IllegalArgumentException("Either icon or iconRes must be provided")
        }
    }
}

