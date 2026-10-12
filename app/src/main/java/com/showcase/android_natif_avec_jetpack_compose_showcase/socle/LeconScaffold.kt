package com.showcase.android_natif_avec_jetpack_compose_showcase.socle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Rendu commun des 40 lecons : en-tete, objectifs, demo interactive, code source,
 * pieges et points-cles. L'ecran scrolle verticalement et les demos recoivent une
 * hauteur bornee via [CarteDemo], ce qui leur permet d'embarquer leur propre Lazy*.
 */
@Composable
fun LeconScaffold(lecon: SpecLecon, modifier: Modifier = Modifier) {
    if (lecon.pleinEcran) {
        // Mini-app (navigation imbriquee, HabitFlow) : la demo prend tout l'espace.
        Column(modifier = modifier.fillMaxSize()) { lecon.demo() }
        return
    }

    var codeOuvert by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        EnTeteLecon(lecon)

        if (lecon.objectifs.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TitreSection(emoji = "🎯", texte = "Objectifs")
                lecon.objectifs.forEach { Puce(it) }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TitreSection(emoji = "🧪", texte = "Demo interactive")
            CarteDemo { lecon.demo() }
        }

        if (lecon.extraits.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TitreSection(
                        emoji = "📄",
                        texte = "Code (${lecon.extraits.size})",
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { codeOuvert = !codeOuvert }) {
                        Text(if (codeOuvert) "Masquer" else "Afficher")
                    }
                }
                SectionRepliable(ouverte = codeOuvert) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        lecon.extraits.forEach { BlocCode(it) }
                    }
                }
            }
        }

        if (lecon.pieges.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TitreSection(emoji = "⚠️", texte = "Pieges frequents")
                lecon.pieges.forEach { CartePiege(it) }
            }
        }

        if (lecon.aRetenir.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TitreSection(emoji = "📌", texte = "En resume")
                lecon.aRetenir.forEach { Puce(it) }
            }
        }
    }
}

@Composable
private fun EnTeteLecon(lecon: SpecLecon) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EtiquetteInfo("Lecon ${lecon.identifiant}")
            EtiquetteInfo("${lecon.dureeMinutes} min")
        }
        Text(text = lecon.titre, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = lecon.resume,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CartePiege(piege: Piege) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = piege.probleme, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "Pourquoi : ${piege.cause}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Remede : ${piege.remede}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
