package com.showcase.android_natif_avec_jetpack_compose_showcase.catalogue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtiquetteInfo

/** Écran racine : la carte des 7 chapitres du cours. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranAccueil(
    surChapitre: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Showcase Jetpack Compose") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { marges ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(marges),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { EnTeteCours() }
            items(items = CHAPITRES, key = { it.numero }) { chapitre ->
                CarteChapitre(chapitre = chapitre, surClic = { surChapitre(chapitre.numero) })
            }
            item { PiedDePage() }
        }
    }
}

@Composable
private fun EnTeteCours() {
    val leconsDisponibles = LECONS_ENREGISTREES.size
    val leconsPlanifiees = PLAN_LECONS.size
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Android natif avec Jetpack Compose",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Chaque leçon du cours dispose ici d'une démo exécutable. Ouvrez un chapitre, " +
                "puis une leçon, et manipulez directement les API qu'elle enseigne.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EtiquetteInfo("7 chapitres")
            EtiquetteInfo("$leconsPlanifiees leçons")
            EtiquetteInfo("60 h")
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Démos disponibles sur cette branche : $leconsDisponibles / $leconsPlanifiees",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { if (leconsPlanifiees == 0) 0f else leconsDisponibles.toFloat() / leconsPlanifiees },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CarteChapitre(chapitre: MetaChapitre, surClic: () -> Unit) {
    val disponibles = LECONS_ENREGISTREES.count { it.chapitre == chapitre.numero }
    Card(
        onClick = surClic,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Chapitre ${chapitre.numero}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                EtiquetteInfo("$disponibles / ${chapitre.nombreLecons} démos")
            }
            Text(text = chapitre.titre, style = MaterialTheme.typography.titleMedium)
            Text(
                text = chapitre.resume,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${chapitre.dureeMinutes / 60} h • ${chapitre.nombreLecons} leçons",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PiedDePage() {
    Text(
        text = "CIA Formation • cours 2a du parcours mobile • démos exécutables, branche par leçon",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp),
    )
}
