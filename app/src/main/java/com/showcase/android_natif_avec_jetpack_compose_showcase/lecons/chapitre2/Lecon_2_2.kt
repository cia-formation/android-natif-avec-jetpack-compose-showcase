package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.BlocCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtiquetteInfo
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_2_2 = SpecLecon(
    chapitre = 2,
    numero = 2,
    titre = "Modifiers : taille, padding, background, click",
    resume = "Maîtriser la chaîne de modifiers de Compose pour dimensionner, espacer, " +
        "colorer et rendre un composable cliquable, sans jamais se tromper dans l'ordre " +
        "d'application.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Appliquer les modifiers de dimensions et d'espacement",
        "Chaîner les modifiers dans le bon ordre",
        "Comprendre pourquoi background avant padding ne donne pas le même rendu qu'après",
        "Gérer les interactions avec Modifier.clickable",
        "Extraire une chaîne de modifiers réutilisable dans une val",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "L'ordre change tout",
            code = """
                // Fond PUIS marge : la marge est intérieure, le fond l'englobe.
                Modifier
                    .background(Color.Blue)
                    .padding(16.dp)

                // Marge PUIS fond : la marge est extérieure, le fond ne la couvre pas.
                Modifier
                    .padding(16.dp)
                    .background(Color.Blue)
            """.trimIndent(),
            commentaire = "Chaque modifier agit sur le résultat du précédent. Lire la " +
                "chaîne de haut en bas suffit à prédire le rendu.",
        ),
        ExtraitCode(
            titre = "Zone cliquable : la position compte aussi",
            code = """
                // clickable AVANT padding : toute la surface, marge comprise, réagit.
                Modifier
                    .clickable { ouvrir() }
                    .padding(16.dp)

                // clickable APRÈS padding : seul le contenu réagit, la marge est morte.
                Modifier
                    .padding(16.dp)
                    .clickable { ouvrir() }
            """.trimIndent(),
        ),
        ExtraitCode(
            titre = "Extraire une chaîne réutilisable",
            code = """
                private val modifierCarte = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(16.dp)
            """.trimIndent(),
            commentaire = "Une chaîne nommée se relit, se teste et se réutilise. Dès cinq " +
                "maillons, extrayez-la.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "La marge apparaît en dehors du fond",
            cause = "padding placé avant background dans la chaîne.",
            remede = "Pour une marge intérieure, appliquer background (ou clip) d'abord, " +
                "padding ensuite. Le bac à sable ci-dessus le montre en un geste.",
        ),
        Piege(
            probleme = "Le clic ne fonctionne que sur le texte",
            cause = "clickable placé en fin de chaîne, après les paddings.",
            remede = "Placer clickable avant les paddings pour que toute la surface réagisse.",
        ),
        Piege(
            probleme = "fillMaxSize sur un composable imbriqué sans parent contraint",
            cause = "Le parent est une Column sans hauteur définie.",
            remede = "Donner une contrainte explicite au parent, ou utiliser fillMaxWidth " +
                "plutôt que fillMaxSize.",
        ),
        Piege(
            probleme = "size(Dp.Infinity)",
            cause = "Copier-coller malencontreux.",
            remede = "Utiliser fillMaxWidth / fillMaxHeight pour « prendre toute la place ».",
        ),
        Piege(
            probleme = "Chaîne de quinze maillons illisible",
            cause = "Accumulation progressive sans regroupement.",
            remede = "Extraire une val nommée, ou découper en plusieurs composables.",
        ),
    ),
    aRetenir = listOf(
        "Un Modifier décrit taille, espacement, couleur et interactions par une chaîne de fonctions",
        "L'ordre des maillons change le rendu : chaque étape agit sur le résultat de la précédente",
        "background avant padding donne une marge intérieure ; l'inverse donne une marge extérieure",
        "clickable avant les paddings étend la zone sensible à toute la surface",
        "Quand un rendu surprend, relisez la chaîne dans l'ordre, maillon par maillon",
    ),
    demo = { DemoModifiers() },
)

/** Un maillon de la chaîne, réordonnable et activable dans le bac à sable. */
private enum class Maillon(val etiquette: String, val code: String) {
    TAILLE("size(120.dp)", ".size(120.dp)"),
    COINS("clip(RoundedCornerShape(16.dp))", ".clip(RoundedCornerShape(16.dp))"),
    FOND("background(primaryContainer)", ".background(couleurFond)"),
    BORDURE("border(2.dp, primary)", ".border(2.dp, couleurBordure, RoundedCornerShape(16.dp))"),
    MARGE("padding(16.dp)", ".padding(16.dp)"),
    CLIC("clickable { }", ".clickable { compteurClics++ }"),
}

@Composable
private fun DemoModifiers() {
    // L'ordre de cette liste EST la chaîne de modifiers : la réordonner change le rendu.
    val ordre = remember {
        mutableStateListOf(
            Maillon.TAILLE,
            Maillon.COINS,
            Maillon.FOND,
            Maillon.MARGE,
            Maillon.CLIC,
        )
    }
    val actifs = remember {
        mutableStateListOf(*Maillon.entries.filter { it != Maillon.BORDURE }.toTypedArray())
    }
    var clics by remember { mutableIntStateOf(0) }

    val couleurFond = MaterialTheme.colorScheme.primaryContainer
    val couleurBordure = MaterialTheme.colorScheme.primary

    // Construction de la chaîne par repliement, dans l'ordre choisi par l'apprenant.
    val modifierConstruit = ordre
        .filter { it in actifs }
        .fold(Modifier as Modifier) { accumule, maillon ->
            when (maillon) {
                Maillon.TAILLE -> accumule.size(120.dp)
                Maillon.COINS -> accumule.clip(RoundedCornerShape(16.dp))
                Maillon.FOND -> accumule.background(couleurFond)
                Maillon.BORDURE -> accumule.border(2.dp, couleurBordure, RoundedCornerShape(16.dp))
                Maillon.MARGE -> accumule.padding(16.dp)
                Maillon.CLIC -> accumule.clickable { clics++ }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "🧮", texte = "Bac à sable : réordonnez la chaîne")
        Text(
            text = "Déplacez les maillons avec les flèches, activez-les avec les cases. " +
                "Le rendu et le code changent en direct.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            ordre.forEachIndexed { index, maillon ->
                LigneMaillon(
                    maillon = maillon,
                    actif = maillon in actifs,
                    peutMonter = index > 0,
                    peutDescendre = index < ordre.lastIndex,
                    surBascule = { if (maillon in actifs) actifs.remove(maillon) else actifs.add(maillon) },
                    surMonter = { ordre.add(index - 1, ordre.removeAt(index)) },
                    surDescendre = { ordre.add(index + 1, ordre.removeAt(index)) },
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    ordre.clear()
                    ordre.addAll(listOf(Maillon.TAILLE, Maillon.COINS, Maillon.FOND, Maillon.MARGE, Maillon.CLIC))
                },
            ) { Text("Marge intérieure") }
            OutlinedButton(
                onClick = {
                    ordre.clear()
                    ordre.addAll(listOf(Maillon.TAILLE, Maillon.MARGE, Maillon.COINS, Maillon.FOND, Maillon.CLIC))
                },
            ) { Text("Le piège classique") }
        }

        TitreSection(emoji = "👁️", texte = "Rendu")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // La zone pointillée matérialise l'emprise totale du composable,
                // marge extérieure comprise : c'est ce qui rend l'ordre visible.
                Box(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(4.dp),
                    ),
                ) {
                    Box(modifier = modifierConstruit) {
                        Text(
                            text = "Touchez",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EtiquetteInfo("$clics clic(s)")
                    EtiquetteInfo(
                        if (Maillon.CLIC in actifs) {
                            "zone sensible : ${positionClic(ordre, actifs)}"
                        } else {
                            "non cliquable"
                        },
                    )
                }
            }
        }

        BlocCode(
            extrait = ExtraitCode(
                titre = "La chaîne que vous venez de construire",
                code = codeGenere(ordre, actifs),
            ),
        )
    }
}

/** Décrit l'étendue de la zone cliquable selon la position de `clickable` dans la chaîne. */
private fun positionClic(ordre: List<Maillon>, actifs: List<Maillon>): String {
    val chaine = ordre.filter { it in actifs }
    val indexClic = chaine.indexOf(Maillon.CLIC)
    val indexMarge = chaine.indexOf(Maillon.MARGE)
    return when {
        indexClic == -1 -> "aucune"
        indexMarge == -1 -> "toute la surface"
        indexClic < indexMarge -> "surface + marge"
        else -> "contenu seul"
    }
}

private fun codeGenere(ordre: List<Maillon>, actifs: List<Maillon>): String {
    val maillons = ordre.filter { it in actifs }
    if (maillons.isEmpty()) return "Box(modifier = Modifier) {\n    Text(\"Touchez\")\n}"
    return buildString {
        appendLine("Box(")
        appendLine("    modifier = Modifier")
        maillons.forEach { appendLine("        ${it.code}") }
        appendLine(") {")
        appendLine("    Text(\"Touchez\")")
        append("}")
    }
}

@Composable
private fun LigneMaillon(
    maillon: Maillon,
    actif: Boolean,
    peutMonter: Boolean,
    peutDescendre: Boolean,
    surBascule: () -> Unit,
    surMonter: () -> Unit,
    surDescendre: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (actif) {
                MaterialTheme.colorScheme.surfaceContainerHighest
            } else {
                MaterialTheme.colorScheme.surfaceContainerLowest
            },
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = actif, onCheckedChange = { surBascule() })
            Text(
                text = maillon.etiquette,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall,
                color = if (actif) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = surMonter, enabled = peutMonter) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Monter ${maillon.etiquette}")
            }
            IconButton(onClick = surDescendre, enabled = peutDescendre) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Descendre ${maillon.etiquette}")
            }
        }
    }
}

@Preview(name = "2.2 clair", showBackground = true)
@Composable
private fun ApercuLecon22Clair() {
    ShowcaseTheme(sombre = false) { DemoModifiers() }
}

@Preview(name = "2.2 sombre", showBackground = true)
@Composable
private fun ApercuLecon22Sombre() {
    ShowcaseTheme(sombre = true) { DemoModifiers() }
}
