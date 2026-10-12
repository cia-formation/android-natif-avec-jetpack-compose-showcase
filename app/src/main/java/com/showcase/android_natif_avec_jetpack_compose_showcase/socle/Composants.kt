package com.showcase.android_natif_avec_jetpack_compose_showcase.socle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Cadre d'une demo interactive. La hauteur est **bornee** : c'est ce qui autorise une
 * demo a contenir sa propre \`LazyColumn\` alors que l'ecran de lecon est deja scrollable
 * (des contraintes infinies dans la meme orientation font planter Compose).
 */
@Composable
fun CarteDemo(
    modifier: Modifier = Modifier,
    hauteurMax: Dp = 520.dp,
    contenu: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = hauteurMax)
                .padding(14.dp),
        ) {
            contenu()
        }
    }
}

/** Titre de section de l'ecran de lecon, avec son emoji repris du cours. */
@Composable
fun TitreSection(emoji: String, texte: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = emoji, style = MaterialTheme.typography.titleMedium)
        Text(
            text = texte,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Puce d'une liste a points (objectifs, a retenir). */
@Composable
fun Puce(texte: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(text = texte, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Selecteur d'une valeur parmi une liste courte, rendu en rangee de FilterChip. */
@Composable
fun <T> SelecteurOption(
    etiquette: String,
    options: List<T>,
    selection: T,
    surSelection: (T) -> Unit,
    modifier: Modifier = Modifier,
    nomOption: (T) -> String = { it.toString() },
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etiquette,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selection,
                    onClick = { surSelection(option) },
                    label = { Text(nomOption(option)) },
                )
            }
        }
    }
}

/** Journal d'evenements d'une demo (appels reseau, recompositions, emissions de Flow). */
@Composable
fun ZoneJournal(
    lignes: List<String>,
    modifier: Modifier = Modifier,
    hauteurMax: Dp = 160.dp,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        if (lignes.isEmpty()) {
            Text(
                text = "Journal vide — declenchez la demo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp),
            )
        } else {
            Column(
                modifier = Modifier
                    .heightIn(max = hauteurMax)
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                lignes.forEach { ligne ->
                    Text(
                        text = ligne,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

/** Ligne cle/valeur, utilisee par les fiches d'introspection du chapitre 1. */
@Composable
fun LigneValeur(cle: String, valeur: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = cle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = valeur,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** Etat vide reutilisable (listes sans donnee, backend non configure). */
@Composable
fun EtatVide(
    icone: ImageVector,
    titre: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = titre, style = MaterialTheme.typography.titleSmall)
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Etiquette d'information compacte. */
@Composable
fun EtiquetteInfo(texte: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
    ) {
        Text(
            text = texte,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

/** Section repliable, utilisee pour le code et les pieges. */
@Composable
fun SectionRepliable(
    ouverte: Boolean,
    contenu: @Composable () -> Unit,
) {
    AnimatedVisibility(visible = ouverte) { contenu() }
}
