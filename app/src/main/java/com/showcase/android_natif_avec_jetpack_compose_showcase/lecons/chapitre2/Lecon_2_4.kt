package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SelecteurOption
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_2_4 = SpecLecon(
    chapitre = 2,
    numero = 4,
    titre = "Texte, images et ressources",
    resume = "Afficher du texte stylé avec Text et TextStyle, charger des images " +
        "(drawable, vector, Coil) et localiser les chaînes avec stringResource pour une " +
        "app Android soignée.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Afficher du texte stylé avec Text et TextStyle",
        "Mélanger plusieurs styles dans un même Text avec AnnotatedString",
        "Intégrer une image : painterResource, Icon vectoriel, AsyncImage distante",
        "Fournir un contentDescription pour l'accessibilité",
        "Localiser les chaînes avec stringResource plutôt que du texte en dur",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Plusieurs styles dans un seul Text",
            code = """
                Text(
                    text = buildAnnotatedString {
                        append("Objectif ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("atteint")
                        }
                        append(" — ")
                        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                            append("en retard")
                        }
                    },
                )
            """.trimIndent(),
            commentaire = "AnnotatedString évite d'empiler trois Text dans une Row pour " +
                "styler trois mots : le texte reste un seul bloc, donc il se coupe " +
                "correctement en fin de ligne.",
        ),
        ExtraitCode(
            titre = "Les trois chemins vers une image",
            code = """
                // 1. Bitmap ou vecteur du projet
                Image(painter = painterResource(R.drawable.illustration), contentDescription = "…")

                // 2. Icône vectorielle Material
                Icon(imageVector = Icons.Outlined.Favorite, contentDescription = "Favori")

                // 3. Image distante, avec Coil
                AsyncImage(
                    model = "https://exemple.test/vignette.png",
                    contentDescription = "Vignette",
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.erreur),
                )
            """.trimIndent(),
            commentaire = "La démo ci-dessus charge réellement une image distante — servie " +
                "par le serveur Ktor embarqué du showcase, donc même en mode avion.",
        ),
        ExtraitCode(
            titre = "sp pour le texte, dp pour les dimensions",
            code = """
                Text(text = "Titre", fontSize = 20.sp)        // suit l'échelle système
                Box(modifier = Modifier.size(48.dp))          // dimension physique stable
            """.trimIndent(),
            commentaire = "sp respecte le réglage d'accessibilité « taille du texte » de " +
                "l'utilisateur ; dp ne le suit pas, ce qui est voulu pour les dimensions.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "contentDescription écrit en dur, souvent en anglais",
            cause = "Oubli du passage par les ressources.",
            remede = "Toujours stringResource(R.string.…) : le lecteur d'écran lit cette " +
                "chaîne, elle doit être localisée comme le reste.",
        ),
        Piege(
            probleme = "Taille de texte exprimée en dp",
            cause = "Réflexe venu du web, où tout est en pixels.",
            remede = "sp pour le texte, dp pour les dimensions. Un texte en dp ignore le " +
                "réglage d'accessibilité de l'utilisateur.",
        ),
        Piege(
            probleme = "Image distante sans placeholder ni état d'erreur",
            cause = "Le réseau est rapide en développement.",
            remede = "Fournir placeholder et error à AsyncImage. La démo laisse déclencher " +
                "les trois états à la main.",
        ),
        Piege(
            probleme = "Bitmaps dupliqués dans chaque dossier de densité",
            cause = "Gestion manuelle des densités, héritée des vieux projets.",
            remede = "Préférer un vecteur XML : un seul fichier, net à toutes les densités.",
        ),
        Piege(
            probleme = "Pluriel construit par concaténation",
            cause = "« 1 article » et « 2 articles » traités à la main.",
            remede = "pluralStringResource avec plurals.xml : les règles de pluriel varient " +
                "selon la langue, et certaines en ont plus de deux.",
        ),
    ),
    aRetenir = listOf(
        "Text accepte un TextStyle, ou un AnnotatedString pour mélanger plusieurs styles",
        "Trois chemins pour les images : painterResource, Icon vectoriel, AsyncImage distante",
        "contentDescription est obligatoire sur chaque image porteuse de sens",
        "stringResource évite le texte en dur et rend la localisation possible",
        "Les tailles de texte s'expriment en sp, les dimensions en dp",
    ),
    demo = { DemoTexteEtImages() },
)

private val POIDS = listOf("Normal", "Medium", "SemiBold", "Bold")
private val TAILLES = listOf("12", "14", "16", "20", "28")

@Composable
private fun DemoTexteEtImages() {
    var poids by remember { mutableIntStateOf(0) }
    var taille by remember { mutableIntStateOf(2) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "✍️", texte = "Texte et TextStyle")

        SelecteurOption(
            etiquette = "fontWeight",
            options = POIDS,
            selection = POIDS[poids],
            surSelection = { poids = POIDS.indexOf(it) },
        )
        SelecteurOption(
            etiquette = "fontSize (en sp)",
            options = TAILLES,
            selection = TAILLES[taille],
            surSelection = { taille = TAILLES.indexOf(it) },
        )

        Text(
            text = "Le texte suit vos réglages, en direct.",
            fontWeight = when (poids) {
                1 -> FontWeight.Medium
                2 -> FontWeight.SemiBold
                3 -> FontWeight.Bold
                else -> FontWeight.Normal
            },
            fontSize = TAILLES[taille].toInt().sp,
        )

        TitreSection(emoji = "🎨", texte = "AnnotatedString : plusieurs styles, un seul Text")
        Text(
            text = buildAnnotatedString {
                append("Habitude ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                ) { append("accomplie") }
                append(" · ")
                withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                    append("en retard")
                }
                append(" · ")
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("optionnelle") }
            },
            style = MaterialTheme.typography.bodyLarge,
        )

        TitreSection(emoji = "🖼️", texte = "Les trois chemins vers une image")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ColonneImage(titre = "Icon vectoriel") {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = "Favori",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            ColonneImage(titre = "Placeholder") {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
            }
            ColonneImage(titre = "État d'erreur") {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = "Image indisponible",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
        Text(
            text = "Le chargement distant avec Coil est démontré au chapitre 6, quand le " +
                "serveur embarqué du showcase est en service : la vignette est alors une " +
                "vraie image PNG servie en HTTP sur la boucle locale.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ColonneImage(titre: String, contenu: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        contenu()
        Text(
            text = titre,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "2.4 clair", showBackground = true)
@Composable
private fun ApercuLecon24Clair() {
    ShowcaseTheme(sombre = false) { DemoTexteEtImages() }
}

@Preview(name = "2.4 sombre", showBackground = true)
@Composable
private fun ApercuLecon24Sombre() {
    ShowcaseTheme(sombre = true) { DemoTexteEtImages() }
}
