package com.example.canasta.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.canasta.ui.components.common.UserAvatar


@Composable
fun ProfileHeader(
    userName: String,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar circular reutilizado de commons

        UserAvatar()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))// es un enter

        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileHeaderPreview() {
    ProfileHeader(
        userName = "Juan Pérez",
        userEmail = "juan.perez@email.com"
    )
}
