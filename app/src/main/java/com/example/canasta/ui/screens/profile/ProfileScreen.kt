package com.example.canasta.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.canasta.ui.components.common.BottomNavBar

@Composable
fun ProfileScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("Profile Screen")
        }
    }
}
