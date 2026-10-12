package com.showcase.android_natif_avec_jetpack_compose_showcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.showcase.android_natif_avec_jetpack_compose_showcase.catalogue.NavigationCatalogue
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity unique du showcase — exactement le modèle enseigné par la leçon 3.1 :
 * une seule `Activity`, toute la navigation côté Compose.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShowcaseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavigationCatalogue()
                }
            }
        }
    }
}
