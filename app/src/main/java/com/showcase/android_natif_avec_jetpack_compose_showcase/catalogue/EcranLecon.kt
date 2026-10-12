package com.showcase.android_natif_avec_jetpack_compose_showcase.catalogue

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.showcase.android_natif_avec_jetpack_compose_showcase.R
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtatVide
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.LeconScaffold

/**
 * Hôte d'une leçon : retrouve sa [SpecLecon] dans le registre généré et délègue le rendu
 * à [LeconScaffold]. Si la démo n'existe pas sur la branche courante, affiche un état vide
 * qui indique sur quelle branche la trouver.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranLecon(
    chapitre: Int,
    numero: Int,
    surRetour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lecon = LECONS_ENREGISTREES.firstOrNull { it.chapitre == chapitre && it.numero == numero }
    val plan = PLAN_LECONS.firstOrNull { it.chapitre == chapitre && it.numero == numero }
    val brancheAttendue = "lecon/ch%02d-l%02d-*".format(chapitre, numero)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Leçon $chapitre.$numero") },
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
        if (lecon != null) {
            LeconScaffold(lecon = lecon, modifier = Modifier.padding(marges))
        } else {
            EtatVide(
                icone = Icons.Outlined.Construction,
                titre = plan?.titre ?: "Leçon inconnue",
                description = "Cette démo n'est pas présente sur la branche courante. " +
                    "Basculez sur $brancheAttendue ou sur main pour l'exécuter.",
                modifier = Modifier.padding(marges),
            )
        }
    }
}
