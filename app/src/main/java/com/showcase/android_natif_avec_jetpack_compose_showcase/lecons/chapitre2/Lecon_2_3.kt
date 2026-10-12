package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.BlocCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SelecteurOption
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_2_3 = SpecLecon(
    chapitre = 2,
    numero = 3,
    titre = "Layouts fondamentaux : Column, Row, Box",
    resume = "Organiser des composables verticalement avec Column, horizontalement avec " +
        "Row et en profondeur avec Box, puis maîtriser l'alignement et l'arrangement des enfants.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Organiser des composables avec Column, Row et Box",
        "Aligner et justifier les enfants avec Arrangement et Alignment",
        "Partager l'espace restant avec Modifier.weight",
        "Empiler des éléments en Z avec Box et align",
        "Reconnaître les imbrications de layouts derrière un écran réel",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Les deux axes d'une Column",
            code = """
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),   // axe principal
                    horizontalAlignment = Alignment.CenterHorizontally,  // axe secondaire
                ) {
                    Text("Premier")
                    Text("Deuxième")
                }
            """.trimIndent(),
            commentaire = "Dans une Column, l'arrangement agit verticalement et " +
                "l'alignement horizontalement. Dans une Row, c'est l'inverse. " +
                "Retenir « arrangement = axe principal » suffit.",
        ),
        ExtraitCode(
            titre = "weight partage l'espace restant",
            code = """
                Row {
                    Box(Modifier.weight(1f))   // 1 part
                    Box(Modifier.weight(2f))   // 2 parts
                    Text("fixe")               // mesuré d'abord, hors partage
                }
            """.trimIndent(),
            commentaire = "weight est une extension de RowScope et ColumnScope : hors d'un " +
                "Row ou d'une Column, elle n'existe pas.",
        ),
        ExtraitCode(
            titre = "Box empile en Z",
            code = """
                Box {
                    Image(...)                                    // dessous
                    Badge(Modifier.align(Alignment.TopEnd))       // dessus, coin haut-droit
                }
            """.trimIndent(),
            commentaire = "L'ordre d'écriture est l'ordre d'empilement : le dernier enfant " +
                "est au-dessus.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "weight utilisé hors d'un Row ou d'une Column",
            cause = "Chaîne de modifiers copiée dans un Box.",
            remede = "weight est une extension sur RowScope et ColumnScope. Dans un Box, " +
                "utilisez matchParentSize ou des contraintes explicites.",
        ),
        Piege(
            probleme = "Column qui déborde de l'écran",
            cause = "Le contenu est plus grand que la hauteur disponible.",
            remede = "Ajouter verticalScroll(rememberScrollState()), ou passer en LazyColumn " +
                "dès une vingtaine d'éléments (leçon 2.6).",
        ),
        Piege(
            probleme = "Un enfant de Box masque tous les autres",
            cause = "Le premier enfant est opaque et en fillMaxSize.",
            remede = "L'ordre d'écriture est l'ordre d'empilement : placez le fond en " +
                "premier, les surcouches ensuite.",
        ),
        Piege(
            probleme = "Des Spacer partout",
            cause = "Réflexe des marges XML.",
            remede = "Arrangement.spacedBy(...) espace tous les enfants d'un coup et reste " +
                "cohérent si la liste change.",
        ),
        Piege(
            probleme = "Row qui tronque son dernier enfant",
            cause = "Un enfant réclame toute la largeur.",
            remede = "Attribuer un weight à l'enfant extensible, ou contraindre sa largeur " +
                "avec widthIn.",
        ),
    ),
    aRetenir = listOf(
        "Column empile verticalement, Row aligne horizontalement, Box superpose en Z",
        "Arrangement pilote l'axe principal, Alignment l'axe secondaire",
        "Modifier.weight partage l'espace restant au prorata, dans Row et Column uniquement",
        "Box et align permettent les surcouches, comme un badge sur une image",
        "Les écrans réels sont des imbrications de ces trois layouts",
    ),
    demo = { DemoLayouts() },
)

private enum class TypeLayout(val etiquette: String) {
    COLUMN("Column"),
    ROW("Row"),
    BOX("Box"),
}

private val ARRANGEMENTS_NOMS = listOf("spacedBy(12.dp)", "Start / Top", "Center", "End / Bottom", "SpaceBetween", "SpaceEvenly")
private val ALIGNEMENTS_NOMS = listOf("Start / Top", "Center", "End / Bottom")
private val ALIGNEMENTS_BOX = listOf("TopStart", "TopEnd", "Center", "BottomStart", "BottomEnd")

@Composable
private fun DemoLayouts() {
    var typeActif by remember { mutableIntStateOf(0) }
    var arrangement by remember { mutableStateOf(ARRANGEMENTS_NOMS.first()) }
    var alignement by remember { mutableStateOf(ALIGNEMENTS_NOMS.first()) }
    var alignementBox by remember { mutableStateOf(ALIGNEMENTS_BOX[1]) }
    var avecPoids by remember { mutableStateOf(false) }

    val type = TypeLayout.entries[typeActif]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "📐", texte = "Playground de layouts")

        PrimaryTabRow(selectedTabIndex = typeActif) {
            TypeLayout.entries.forEachIndexed { index, valeur ->
                Tab(
                    selected = index == typeActif,
                    onClick = { typeActif = index },
                    text = { Text(valeur.etiquette) },
                )
            }
        }

        when (type) {
            TypeLayout.COLUMN, TypeLayout.ROW -> {
                SelecteurOption(
                    etiquette = if (type == TypeLayout.COLUMN) {
                        "verticalArrangement (axe principal)"
                    } else {
                        "horizontalArrangement (axe principal)"
                    },
                    options = ARRANGEMENTS_NOMS,
                    selection = arrangement,
                    surSelection = { arrangement = it },
                )
                SelecteurOption(
                    etiquette = if (type == TypeLayout.COLUMN) {
                        "horizontalAlignment (axe secondaire)"
                    } else {
                        "verticalAlignment (axe secondaire)"
                    },
                    options = ALIGNEMENTS_NOMS,
                    selection = alignement,
                    surSelection = { alignement = it },
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Switch(checked = avecPoids, onCheckedChange = { avecPoids = it })
                    Text(
                        text = "weight(1f, 2f, 3f) sur les trois enfants",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            TypeLayout.BOX -> {
                SelecteurOption(
                    etiquette = "align du badge",
                    options = ALIGNEMENTS_BOX,
                    selection = alignementBox,
                    surSelection = { alignementBox = it },
                )
            }
        }

        TitreSection(emoji = "👁️", texte = "Rendu")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(10.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp)),
            ) {
                when (type) {
                    TypeLayout.COLUMN -> RenduColumn(arrangement, alignement, avecPoids)
                    TypeLayout.ROW -> RenduRow(arrangement, alignement, avecPoids)
                    TypeLayout.BOX -> RenduBox(alignementBox)
                }
            }
        }

        BlocCode(
            extrait = ExtraitCode(
                titre = "Le code correspondant",
                code = when (type) {
                    TypeLayout.COLUMN -> codeLineaire("Column", "vertical", "horizontal", arrangement, alignement, avecPoids)
                    TypeLayout.ROW -> codeLineaire("Row", "horizontal", "vertical", arrangement, alignement, avecPoids)
                    TypeLayout.BOX -> codeBox(alignementBox)
                },
            ),
        )
    }
}

private fun arrangementVertical(nom: String): Arrangement.Vertical = when (nom) {
    "Center" -> Arrangement.Center
    "End / Bottom" -> Arrangement.Bottom
    "SpaceBetween" -> Arrangement.SpaceBetween
    "SpaceEvenly" -> Arrangement.SpaceEvenly
    "spacedBy(12.dp)" -> Arrangement.spacedBy(12.dp)
    else -> Arrangement.Top
}

private fun arrangementHorizontal(nom: String): Arrangement.Horizontal = when (nom) {
    "Center" -> Arrangement.Center
    "End / Bottom" -> Arrangement.End
    "SpaceBetween" -> Arrangement.SpaceBetween
    "SpaceEvenly" -> Arrangement.SpaceEvenly
    "spacedBy(12.dp)" -> Arrangement.spacedBy(12.dp)
    else -> Arrangement.Start
}

private fun alignementHorizontal(nom: String): Alignment.Horizontal = when (nom) {
    "Center" -> Alignment.CenterHorizontally
    "End / Bottom" -> Alignment.End
    else -> Alignment.Start
}

private fun alignementVertical(nom: String): Alignment.Vertical = when (nom) {
    "Center" -> Alignment.CenterVertically
    "End / Bottom" -> Alignment.Bottom
    else -> Alignment.Top
}

private fun alignementBoite(nom: String): Alignment = when (nom) {
    "TopEnd" -> Alignment.TopEnd
    "Center" -> Alignment.Center
    "BottomStart" -> Alignment.BottomStart
    "BottomEnd" -> Alignment.BottomEnd
    else -> Alignment.TopStart
}

private val COULEURS_ENFANTS = listOf(
    Color(0xFF3B82F6),
    Color(0xFF10B981),
    Color(0xFFF59E0B),
)

@Composable
private fun RenduColumn(arrangement: String, alignement: String, avecPoids: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().then(if (avecPoids) Modifier.height(200.dp) else Modifier),
        verticalArrangement = arrangementVertical(arrangement),
        horizontalAlignment = alignementHorizontal(alignement),
    ) {
        COULEURS_ENFANTS.forEachIndexed { index, couleur ->
            Box(
                modifier = Modifier
                    .then(
                        if (avecPoids) {
                            Modifier.weight((index + 1).toFloat()).fillMaxWidth(0.5f)
                        } else {
                            Modifier.size(width = 90.dp, height = 36.dp)
                        },
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .background(couleur),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (avecPoids) "weight(${index + 1}f)" else "enfant ${index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun RenduRow(arrangement: String, alignement: String, avecPoids: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        horizontalArrangement = arrangementHorizontal(arrangement),
        verticalAlignment = alignementVertical(alignement),
    ) {
        COULEURS_ENFANTS.forEachIndexed { index, couleur ->
            Box(
                modifier = Modifier
                    .then(
                        if (avecPoids) {
                            Modifier.weight((index + 1).toFloat())
                        } else {
                            Modifier.size(width = 70.dp, height = 60.dp)
                        },
                    )
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(couleur),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (avecPoids) "${index + 1}f" else "${index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun RenduBox(alignementBadge: String) {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "fond (premier enfant)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Box(
            modifier = Modifier
                .align(alignementBoite(alignementBadge))
                .padding(10.dp)
                .size(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(COULEURS_ENFANTS[2]),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "badge",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
            )
        }
    }
}

private fun codeLineaire(
    layout: String,
    axePrincipal: String,
    axeSecondaire: String,
    arrangement: String,
    alignement: String,
    avecPoids: Boolean,
): String {
    val valeurArrangement = when (arrangement) {
        "spacedBy(12.dp)" -> "Arrangement.spacedBy(12.dp)"
        "Start / Top" -> if (layout == "Row") "Arrangement.Start" else "Arrangement.Top"
        "End / Bottom" -> if (layout == "Row") "Arrangement.End" else "Arrangement.Bottom"
        else -> "Arrangement.$arrangement"
    }
    val valeurAlignement = when (alignement) {
        "Center" -> if (layout == "Row") "Alignment.CenterVertically" else "Alignment.CenterHorizontally"
        "End / Bottom" -> if (layout == "Row") "Alignment.Bottom" else "Alignment.End"
        else -> if (layout == "Row") "Alignment.Top" else "Alignment.Start"
    }
    val suffixePrincipal = if (axePrincipal == "vertical") "verticalArrangement" else "horizontalArrangement"
    val suffixeSecondaire = if (axeSecondaire == "horizontal") "horizontalAlignment" else "verticalAlignment"
    val enfant = if (avecPoids) {
        "        Box(Modifier.weight(1f).background(bleu))\n" +
            "        Box(Modifier.weight(2f).background(vert))\n" +
            "        Box(Modifier.weight(3f).background(ambre))"
    } else {
        "        Box(Modifier.size(90.dp, 36.dp).background(bleu))\n" +
            "        Box(Modifier.size(90.dp, 36.dp).background(vert))\n" +
            "        Box(Modifier.size(90.dp, 36.dp).background(ambre))"
    }
    return """
        $layout(
            $suffixePrincipal = $valeurArrangement,
            $suffixeSecondaire = $valeurAlignement,
        ) {
        $enfant
        }
    """.trimIndent()
}

private fun codeBox(alignement: String): String = """
    Box {
        // L'ordre d'écriture est l'ordre d'empilement.
        Box(Modifier.fillMaxSize().background(primaryContainer))   // dessous
        Box(
            Modifier
                .align(Alignment.$alignement)                      // dessus
                .padding(10.dp)
                .size(54.dp)
                .background(ambre),
        )
    }
""".trimIndent()

@Preview(name = "2.3 clair", showBackground = true)
@Composable
private fun ApercuLecon23Clair() {
    ShowcaseTheme(sombre = false) { DemoLayouts() }
}

@Preview(name = "2.3 sombre", showBackground = true)
@Composable
private fun ApercuLecon23Sombre() {
    ShowcaseTheme(sombre = true) { DemoLayouts() }
}
