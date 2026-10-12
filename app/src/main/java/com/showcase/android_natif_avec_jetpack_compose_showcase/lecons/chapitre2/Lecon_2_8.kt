package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtiquetteInfo
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_2_8 = SpecLecon(
    chapitre = 2,
    numero = 8,
    titre = "Animations simples et transitions",
    resume = "Animer une valeur avec animate*AsState, faire apparaître un composable avec " +
        "AnimatedVisibility et orchestrer une transition entre deux états avec Crossfade.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Animer une valeur avec animateFloatAsState, animateDpAsState, animateColorAsState",
        "Faire apparaître et disparaître un composable avec AnimatedVisibility",
        "Enchaîner deux rendus exclusifs en fondu avec Crossfade",
        "Choisir une durée d'animation qui sert la lecture",
        "Reconnaître les animations qui coûtent cher en performance",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "animate*AsState : une valeur dérivée d'un état",
            code = """
                var etendu by remember { mutableStateOf(false) }

                // La valeur cible vient de l'état ; Compose interpole le reste.
                val hauteur by animateDpAsState(
                    targetValue = if (etendu) 180.dp else 80.dp,
                    animationSpec = tween(durationMillis = 250),
                    label = "hauteur-carte",
                )

                Box(Modifier.height(hauteur))
            """.trimIndent(),
            commentaire = "C'est l'animation déclarative : on ne pilote pas la frame par " +
                "frame, on déclare la cible et la durée.",
        ),
        ExtraitCode(
            titre = "AnimatedVisibility : apparition et disparition",
            code = """
                AnimatedVisibility(
                    visible = afficheDetail,
                    enter = fadeIn(tween(200)) + slideInVertically { it / 2 },
                    exit = fadeOut(tween(150)) + slideOutVertically { it / 2 },
                ) {
                    CarteDetail()
                }
            """.trimIndent(),
            commentaire = "La sortie est volontairement plus courte que l'entrée : on " +
                "attend l'apparition, on ne veut pas attendre la disparition.",
        ),
        ExtraitCode(
            titre = "Crossfade : deux rendus mutuellement exclusifs",
            code = """
                Crossfade(targetState = etat, label = "etat-ecran") { courant ->
                    when (courant) {
                        Etat.CHARGEMENT -> Indicateur()
                        Etat.SUCCES -> Contenu()
                        Etat.ERREUR -> MessageErreur()
                    }
                }
            """.trimIndent(),
            commentaire = "Exactement le cas d'un UiState scellé Loading/Success/Error, " +
                "que la leçon 4.4 formalise.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "L'animation se relance à chaque recomposition",
            cause = "La valeur cible est recalculée au lieu d'être dérivée d'un état mémorisé.",
            remede = "La cible doit venir d'un State. Si elle change à chaque rendu, " +
                "l'animation ne converge jamais.",
        ),
        Piege(
            probleme = "Durées au-delà de 500 ms",
            cause = "Enthousiasme pour l'effet obtenu.",
            remede = "Rester entre 150 et 300 ms. Le curseur de la démo permet de sentir " +
                "le moment où l'interface devient lente.",
        ),
        Piege(
            probleme = "Animer la structure d'une liste entière",
            cause = "Vouloir animer l'ajout et le retrait de tous les éléments.",
            remede = "Animer des propriétés (alpha, taille, couleur), pas la structure. " +
                "Pour les listes, animateItem avec une clé stable suffit.",
        ),
        Piege(
            probleme = "AnimatedVisibility dans une liste sans clé",
            cause = "Pas de key sur items.",
            remede = "Sans clé stable, Compose perd l'identité des éléments et les " +
                "animations se déclenchent sur le mauvais.",
        ),
        Piege(
            probleme = "Animation infinie décorative",
            cause = "Un effet impressionnant en démo.",
            remede = "Réserver les boucles infinies aux indicateurs de chargement. " +
                "Ailleurs, elles fatiguent et consomment de la batterie.",
        ),
    ),
    aRetenir = listOf(
        "animate*AsState anime une valeur (Float, Dp, Color) dès que l'état cible change",
        "AnimatedVisibility gère apparition et disparition, avec des specs d'entrée et de sortie distinctes",
        "Crossfade enchaîne en fondu deux rendus mutuellement exclusifs",
        "Les durées utiles se situent entre 150 et 300 ms",
        "Une animation doit servir la lecture : guider, expliquer, polir — jamais décorer",
    ),
    demo = { DemoAnimations() },
)

private enum class EtatEcran(val etiquette: String) {
    CHARGEMENT("Chargement"),
    SUCCES("Succès"),
    ERREUR("Erreur"),
}

@Composable
private fun DemoAnimations() {
    var duree by remember { mutableFloatStateOf(250f) }
    var etendu by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(true) }
    var etat by remember { mutableStateOf(EtatEcran.CHARGEMENT) }

    val specDuree = duree.toInt()

    // Trois valeurs dérivées du même état, chacune animée dans son type.
    val hauteur by animateDpAsState(
        targetValue = if (etendu) 150.dp else 72.dp,
        animationSpec = tween(specDuree),
        label = "hauteur",
    )
    val opacite by animateFloatAsState(
        targetValue = if (etendu) 1f else 0.55f,
        animationSpec = tween(specDuree),
        label = "opacite",
    )
    val couleur by animateColorAsState(
        targetValue = if (etendu) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
        animationSpec = tween(specDuree),
        label = "couleur",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "⏱️", texte = "Durée : $specDuree ms")
        Slider(
            value = duree,
            onValueChange = { duree = it },
            valueRange = 50f..1_200f,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = when {
                specDuree < 150 -> "Très court : l'animation est presque invisible."
                specDuree <= 300 -> "Zone utile : l'œil suit sans attendre."
                specDuree <= 500 -> "Déjà perceptible comme une lenteur."
                else -> "Trop long : l'interface donne l'impression de ramer."
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (specDuree > 500) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )

        TitreSection(emoji = "📐", texte = "animate*AsState")
        Button(onClick = { etendu = !etendu }) {
            Text(if (etendu) "Replier" else "Déplier")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(hauteur)
                .clip(RoundedCornerShape(12.dp))
                .background(couleur)
                .alpha(opacite),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Dp, Color et Float animés ensemble",
                style = MaterialTheme.typography.labelMedium,
            )
        }

        TitreSection(emoji = "👁️", texte = "AnimatedVisibility")
        Button(onClick = { visible = !visible }) {
            Text(if (visible) "Masquer" else "Afficher")
        }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(specDuree)) + slideInVertically(tween(specDuree)) { it / 2 },
            // La sortie est plus courte : on attend une apparition, pas une disparition.
            exit = fadeOut(tween(specDuree / 2)) + slideOutVertically(tween(specDuree / 2)) { it / 2 },
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            ) {
                Text(
                    text = "Entrée : fondu + glissement sur $specDuree ms.\n" +
                        "Sortie : moitié de la durée, soit ${specDuree / 2} ms.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        TitreSection(emoji = "🔀", texte = "Crossfade entre trois états")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EtatEcran.entries.forEach { valeur ->
                Button(onClick = { etat = valeur }) { Text(valeur.etiquette) }
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
        ) {
            Crossfade(targetState = etat, animationSpec = tween(specDuree), label = "etat-ecran") { courant ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    when (courant) {
                        EtatEcran.CHARGEMENT -> Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            Text("Chargement…", style = MaterialTheme.typography.bodySmall)
                        }
                        EtatEcran.SUCCES -> Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            EtiquetteInfo("5 habitudes")
                            Text("Contenu chargé", style = MaterialTheme.typography.bodyMedium)
                        }
                        EtatEcran.ERREUR -> Text(
                            text = "Réseau indisponible",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
        Text(
            text = "Ces trois états sont exactement ceux d'un UiState scellé — la " +
                "leçon 4.4 les formalise, et le chapitre 6 les branche sur un vrai réseau.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "2.8 clair", showBackground = true)
@Composable
private fun ApercuLecon28Clair() {
    ShowcaseTheme(sombre = false) { DemoAnimations() }
}

@Preview(name = "2.8 sombre", showBackground = true)
@Composable
private fun ApercuLecon28Sombre() {
    ShowcaseTheme(sombre = true) { DemoAnimations() }
}
