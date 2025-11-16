package com.example.canasta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.canasta.ui.navigation.AppNavigation
import com.example.canasta.ui.theme.CanastaTheme
import com.example.canasta.utils.LanguageManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplicar el idioma guardado
        LanguageManager.applyLanguage(this)

        enableEdgeToEdge()
        setContent {
            // Observar cambios de idioma para forzar recomposición
            val languageChangeCounter by LanguageManager.languageChangeCounter.collectAsState()
            val context = LocalContext.current

            // Aplicar idioma cuando cambie
            LaunchedEffect(languageChangeCounter) {
                if (languageChangeCounter > 0) {
                    LanguageManager.applyLanguage(context)
                }
            }

            CanastaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // AppNavigation ahora es el responsable de mostrar la pantalla correcta
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CanastaTheme {
        Greeting("Android")
    }
}