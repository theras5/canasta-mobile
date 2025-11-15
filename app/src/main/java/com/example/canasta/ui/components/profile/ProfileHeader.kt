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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.canasta.R
import com.example.canasta.ui.components.common.UserAvatar
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ProfileHeader(
    userName: String,
    userEmail: String,
    memberSince: String? = null,
    avatarIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    // Formatear la fecha para mostrar "Miembro desde [mes] del [año]"
    val formattedDate = memberSince?.let {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(it)
            // Usar el locale actual para formatear la fecha
            val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            date?.let { d -> outputFormat.format(d) }
        } catch (e: Exception) {
            null
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar circular reutilizado de commons
        UserAvatar(avatarIndex = avatarIndex)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // Mostrar "Miembro desde [mes] del [año]"
        if (formattedDate != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.member_since, formattedDate),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                color = Color.Gray.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileHeaderPreview() {
    ProfileHeader(
        userName = "Juan Pérez",
        userEmail = "juan.perez@email.com",
        memberSince = "2024-01-15"
    )
}
