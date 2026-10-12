package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtiquetteInfo
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_2_5 = SpecLecon(
    chapitre = 2,
    numero = 5,
    titre = "Material 3 et thème personnalisé",
    resume = "Appliquer un MaterialTheme, personnaliser couleurs, typographies et formes, " +
        "puis basculer entre light et dark mode dynamiquement pour une identité visuelle " +
        "cohérente.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Appliquer un MaterialTheme autour de toute l'application",
        "Personnaliser colorScheme, typography et shapes",
        "Respecter les couples de couleurs (primary / onPrimary) pour le contraste",
        "Basculer light et dark mode avec isSystemInDarkTheme",
        "Activer le Dynamic Color sur Android 12+ en gardant un repli",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Les trois tokens d'un MaterialTheme",
            code = """
                MaterialTheme(
                    colorScheme = schema,       // couleurs et leurs couples « on »
                    typography = typographie,   // hiérarchie de styles de texte
                    shapes = formes,            // rayons de coins par taille
                    content = content,
                )
            """.trimIndent(),
            commentaire = "C'est exactement la structure de ShowcaseTheme, le thème de " +
                "cette application. Vous en manipulez les valeurs ci-dessous.",
        ),
        ExtraitCode(
            titre = "Les couples de couleurs",
            code = """
                // Chaque couleur de fond a sa couleur de contenu associée.
                Surface(color = MaterialTheme.colorScheme.primary) {
                    Text(
                        text = "Lisible dans les deux thèmes",
                        color = MaterialTheme.colorScheme.onPrimary,   // le couple
                    )
                }
            """.trimIndent(),
            commentaire = "Utiliser onPrimary sur un fond primary garantit le contraste en " +
                "clair comme en sombre. C'est ce qui évite le texte blanc sur fond blanc.",
        ),
        ExtraitCode(
            titre = "Dynamic Color, avec son garde-fou",
            code = """
                val schema = when {
                    couleurDynamique && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                        if (sombre) dynamicDarkColorScheme(contexte)
                        else dynamicLightColorScheme(contexte)
                    sombre -> SchemaSombre
                    else -> SchemaClair
                }
            """.trimIndent(),
            commentaire = "Sans le test de version, l'appel plante sous Android 12. " +
                "Le showcase désactive le Dynamic Color par défaut pour que la palette du " +
                "cours reste reconnaissable d'un appareil à l'autre.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "Couleurs écrites en dur dans les écrans",
            cause = "Copier-coller rapide d'une maquette.",
            remede = "Toujours MaterialTheme.colorScheme.xxx. Une couleur en dur ne suit " +
                "ni le dark mode ni un changement de charte.",
        ),
        Piege(
            probleme = "Texte blanc sur fond blanc en dark mode",
            cause = "Le couple de couleurs n'a pas été respecté.",
            remede = "Sur un fond primary, le texte est onPrimary. La démo permet de " +
                "basculer en sombre pour le vérifier immédiatement.",
        ),
        Piege(
            probleme = "Thème défini mais pas englobant",
            cause = "Le MaterialTheme a été oublié autour du Scaffold.",
            remede = "Envelopper tout le contenu de setContent { }. Hors du thème, les " +
                "composables retombent sur les valeurs par défaut de Material.",
        ),
        Piege(
            probleme = "fontSize = 16.sp répété partout",
            cause = "Typographie figée au fil de l'écriture.",
            remede = "Passer par MaterialTheme.typography.bodyLarge : un seul endroit à " +
                "changer, et la hiérarchie reste cohérente.",
        ),
        Piege(
            probleme = "Dynamic Color appelé sans garde de version",
            cause = "Exemple copié sans son test de SDK.",
            remede = "dynamicLightColorScheme n'existe qu'à partir d'Android 12 (API 31).",
        ),
    ),
    aRetenir = listOf(
        "MaterialTheme expose trois tokens : colorScheme, typography et shapes",
        "Chaque couleur a son couple de contenu (primary / onPrimary) qui garantit le contraste",
        "isSystemInDarkTheme et un darkColorScheme suffisent au dark mode",
        "Le Dynamic Color (Android 12+) aligne la palette sur le fond d'écran, avec repli obligatoire",
        "Ne mettez jamais de couleur ni de taille en dur dans un écran : passez par le thème",
    ),
    demo = { DemoTheme() },
)

@Composable
private fun DemoTheme() {
    var sombre by remember { mutableStateOf(false) }
    var dynamique by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "🎨", texte = "Pilotez le thème")

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Switch(checked = sombre, onCheckedChange = { sombre = it })
            Text(
                text = if (sombre) "Schéma sombre" else "Schéma clair",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Switch(
                checked = dynamique,
                onCheckedChange = { dynamique = it },
                enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            )
            Column {
                Text(text = "Dynamic Color", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        "Disponible : la palette suivra votre fond d'écran."
                    } else {
                        "Indisponible sous Android 12 — d'où le garde-fou dans le code."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        TitreSection(emoji = "🔍", texte = "Aperçu sous le thème choisi")
        Text(
            text = "La zone ci-dessous est enveloppée dans son propre ShowcaseTheme : " +
                "elle change sans toucher au reste de l'application. C'est exactement ce " +
                "que fait un thème englobant.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        ShowcaseTheme(sombre = sombre, couleurDynamique = dynamique) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Habitudes du jour",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "typography.bodyMedium sur colorScheme.onBackground",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EtiquetteInfo("secondaryContainer")
                        Button(onClick = {}) { Text("primary") }
                    }
                    TitreSection(emoji = "🧱", texte = "Couples de couleurs")
                    GrilleCouples()
                    TitreSection(emoji = "◻️", texte = "Formes")
                    RangeeFormes()
                }
            }
        }
    }
}

@Composable
private fun GrilleCouples() {
    val couples = listOf(
        Triple("primary", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary),
        Triple("secondary", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary),
        Triple("tertiary", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary),
        Triple("error", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError),
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        couples.forEach { (nom, fond, contenu) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(fond)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "$nom / on${nom.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.labelMedium,
                    color = contenu,
                )
            }
        }
    }
}

@Composable
private fun RangeeFormes() {
    val formes = listOf(
        "extraSmall" to MaterialTheme.shapes.extraSmall,
        "small" to MaterialTheme.shapes.small,
        "medium" to MaterialTheme.shapes.medium,
        "large" to MaterialTheme.shapes.large,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        formes.forEach { (nom, forme) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(forme)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                )
                Text(
                    text = nom,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "2.5 clair", showBackground = true)
@Composable
private fun ApercuLecon25Clair() {
    ShowcaseTheme(sombre = false) { DemoTheme() }
}

@Preview(name = "2.5 sombre", showBackground = true)
@Composable
private fun ApercuLecon25Sombre() {
    ShowcaseTheme(sombre = true) { DemoTheme() }
}
