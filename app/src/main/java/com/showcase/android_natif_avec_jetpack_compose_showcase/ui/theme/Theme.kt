package com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SchemaClair = lightColorScheme(
    primary = BleuComposeSombre,
    onPrimary = Color.White,
    primaryContainer = BleuComposeClair,
    onPrimaryContainer = Ardoise900,
    secondary = VertStructureSombre,
    onSecondary = Color.White,
    secondaryContainer = VertStructureClair,
    onSecondaryContainer = Ardoise900,
    tertiary = AmbreDonneesSombre,
    onTertiary = Color.White,
    tertiaryContainer = AmbreDonneesClair,
    onTertiaryContainer = Ardoise900,
    error = RougePiege,
    onError = Color.White,
    errorContainer = RougePiegeClair,
    onErrorContainer = Ardoise900,
    background = Ardoise50,
    onBackground = Ardoise900,
    surface = Ardoise50,
    onSurface = Ardoise900,
    surfaceVariant = Ardoise100,
    onSurfaceVariant = Ardoise500,
    outlineVariant = Color(0xFFD9E0EA),
)

private val SchemaSombre = darkColorScheme(
    primary = BleuComposeClair,
    onPrimary = Ardoise900,
    primaryContainer = BleuComposeSombre,
    onPrimaryContainer = Color.White,
    secondary = VertStructureClair,
    onSecondary = Ardoise900,
    secondaryContainer = VertStructureSombre,
    onSecondaryContainer = Color.White,
    tertiary = AmbreDonneesClair,
    onTertiary = Ardoise900,
    tertiaryContainer = AmbreDonneesSombre,
    onTertiaryContainer = Color.White,
    error = RougePiegeClair,
    onError = Ardoise900,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color.White,
    background = Ardoise900,
    onBackground = Ardoise50,
    surface = Ardoise900,
    onSurface = Ardoise50,
    surfaceVariant = Ardoise800,
    onSurfaceVariant = Color(0xFFA9B6C7),
    outlineVariant = Color(0xFF334155),
)

/**
 * Thème du showcase. [couleurDynamique] est désactivée par défaut : la palette du cours
 * doit rester reconnaissable d'un appareil à l'autre. La leçon 2.5 la rebascule en direct.
 */
@Composable
fun ShowcaseTheme(
    sombre: Boolean = isSystemInDarkTheme(),
    couleurDynamique: Boolean = false,
    content: @Composable () -> Unit,
) {
    val schema = when {
        couleurDynamique && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val contexte = LocalContext.current
            if (sombre) dynamicDarkColorScheme(contexte) else dynamicLightColorScheme(contexte)
        }
        sombre -> SchemaSombre
        else -> SchemaClair
    }
    MaterialTheme(
        colorScheme = schema,
        typography = TypographieShowcase,
        shapes = FormesShowcase,
        content = content,
    )
}
