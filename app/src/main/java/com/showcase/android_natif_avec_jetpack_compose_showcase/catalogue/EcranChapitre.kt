package com.showcase.android_natif_avec_jetpack_compose_showcase.catalogue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.R
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtiquetteInfo

/** Liste des leçons d'un chapitre : celles démontrées sur la branche, et celles à venir. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranChapitre(
    numeroChapitre: Int,
    surLecon: (Int, Int) -> Unit,
    surRetour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val chapitre = chapitreParNumero(numeroChapitre)
    val lecons = planDuChapitre(numeroChapitre)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Chapitre $numeroChapitre") },
                navigationIcon = {
                    IconButton(onClick = surRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_retour))
                    }
                },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (chapitre != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = chapitre.titre, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = chapitre.resume,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            EtiquetteInfo("${chapitre.dureeMinutes / 60} h")
                            EtiquetteInfo("${chapitre.nombreLecons} leçons")
                        }
                    }
                }
            }
            items(items = lecons, key = { it.identifiant }) { plan ->
                val demoPresente = LECONS_ENREGISTREES.any {
                    it.chapitre == plan.chapitre && it.numero == plan.numero
                }
                CarteLecon(
                    plan = plan,
                    demoPresente = demoPresente,
                    surClic = { surLecon(plan.chapitre, plan.numero) },
                )
            }
        }
    }
}

@Composable
private fun CarteLecon(plan: PlanLecon, demoPresente: Boolean, surClic: () -> Unit) {
    val couleurFond = if (demoPresente) {
        MaterialTheme.colorScheme.surfaceContainerLow
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    Card(
        onClick = surClic,
        enabled = demoPresente,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = couleurFond),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = plan.identifiant,
                style = MaterialTheme.typography.titleMedium,
                color = if (demoPresente) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = plan.titre, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${plan.dureeMinutes} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!demoPresente) EtiquetteInfo("à venir")
        }
    }
}
