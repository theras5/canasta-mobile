package com.example.canasta.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.components.profile.AvatarOption

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    avatarIndex: Int = 0
) {
    val avatarOption = AvatarOption.entries.getOrNull(avatarIndex) ?: AvatarOption.GREEN_DARK

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(avatarOption.color),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = avatarOption.icon,
            contentDescription = "Avatar",
            tint = Color.White,
            modifier = Modifier.size(size * 0.6f) // 60% del tamaño del avatar
        )
    }
}
