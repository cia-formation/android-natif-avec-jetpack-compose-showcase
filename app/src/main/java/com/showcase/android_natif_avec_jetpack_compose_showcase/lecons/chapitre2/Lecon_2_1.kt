package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtiquetteInfo
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_2_1 = SpecLecon(
    chapitre = 2,
    numero = 1,
    titre = "Paradigme déclaratif et premier composable",
    resume = "Faire le basculement mental entre UI impérative XML et UI déclarative " +
        "Compose, écrire sa première fonction @Composable et itérer visuellement avec @Preview.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Distinguer une UI impérative (XML + findViewById) d'une UI déclarative (Compose)",
        "Écrire une fonction @Composable qui rend un écran simple",
        "Utiliser @Preview pour itérer sur un rendu sans lancer l'émulateur",
        "Expliquer ce qu'est une recomposition et quand elle se déclenche",
        "Reconnaître l'arbre d'appels de composables derrière un écran",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Premier composable",
            code = """
                @Composable
                fun Bienvenue(prenomUtilisateur: String) {
                    Text(text = "Bonjour ${'$'}prenomUtilisateur")
                }
            """.trimIndent(),
            commentaire = "@Composable marque la fonction comme un nœud de l'arbre UI. " +
                "Le paramètre est l'entrée, le rendu est la sortie. Aucun findViewById.",
        ),
        ExtraitCode(
            titre = "Composer des briques",
            code = """
                @Composable
                fun CarteProfil(prenom: String, role: String) {
                    Column {
                        Text(text = prenom)
                        Text(text = role)
                    }
                }
            """.trimIndent(),
        ),
        ExtraitCode(
            titre = "Compter les recompositions (idiome de démo)",
            code = """
                @Composable
                private fun compteurRecompositions(): Int {
                    val compteur = remember { intArrayOf(0) }
                    compteur[0]++      // s'incrémente à chaque (re)composition
                    return compteur[0]
                }
            """.trimIndent(),
            commentaire = "Muter pendant la composition est à éviter en production : c'est " +
                "ici un instrument de mesure, pour rendre visible ce que le framework fait " +
                "normalement sans qu'on le voie.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "Oublier @Composable sur une fonction appelée",
            cause = "L'erreur du compilateur est peu lisible.",
            remede = "Lire le message « @Composable invocation can only happen from the " +
                "context of a @Composable function » : il désigne exactement la fonction à annoter.",
        ),
        Piege(
            probleme = "Nommer un composable en minuscule (monEcran)",
            cause = "Habitude des fonctions Kotlin classiques.",
            remede = "Toujours PascalCase pour un composable qui émet de l'UI. Un composable " +
                "qui renvoie une valeur, lui, garde le camelCase.",
        ),
        Piege(
            probleme = "Mettre de la logique lourde dans le composable",
            cause = "Code transposé depuis une Activity.",
            remede = "Un composable peut être rejoué des dizaines de fois par seconde. " +
                "Tout calcul coûteux va dans un ViewModel ou derrière un remember.",
        ),
        Piege(
            probleme = "@Preview sur un composable qui prend des paramètres",
            cause = "Le preview ne sait pas quelles valeurs passer.",
            remede = "Créer un wrapper sans paramètre qui fournit des valeurs d'exemple.",
        ),
        Piege(
            probleme = "Confondre recomposition et redémarrage d'écran",
            cause = "Héritage du modèle Activity et de ses rotations.",
            remede = "La recomposition est locale et bon marché : seuls les composables qui " +
                "lisent l'état modifié sont rejoués. La démo le montre compteur en main.",
        ),
    ),
    aRetenir = listOf(
        "En Compose, l'UI est décrite par des fonctions qui prennent un état en paramètre",
        "Une fonction @Composable porte son annotation et son nom en PascalCase",
        "@Preview affiche le rendu en direct dans Android Studio, sans émulateur",
        "La recomposition rejoue automatiquement les composables concernés quand l'état change",
        "La recomposition est ciblée : seuls les composables qui lisent l'état modifié sont rejoués",
    ),
    demo = { DemoParadigmeDeclaratif() },
)

/**
 * Compte les (re)compositions du composable appelant.
 *
 * Idiome de démonstration : on mute un tableau mémorisé pendant la composition, ce qui est
 * à proscrire en production. Ici c'est l'instrument de mesure qui rend visible le travail
 * du framework — et c'est précisément ce que la leçon cherche à faire comprendre.
 */
@Composable
private fun compteurRecompositions(): Int {
    val compteur = remember { intArrayOf(0) }
    compteur[0]++
    return compteur[0]
}

@Composable
private fun DemoParadigmeDeclaratif() {
    var compteur by remember { mutableIntStateOf(0) }
    var modeImperatif by remember { mutableStateOf(false) }
    // En mode « impératif », l'affichage ne suit pas l'état : il faut le rafraîchir à la main,
    // exactement comme un setText() oublié dans le monde XML.
    var affichageManuel by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "🔄", texte = "Impératif ou déclaratif")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(checked = modeImperatif, onCheckedChange = { modeImperatif = it })
            Text(
                text = if (modeImperatif) {
                    "Mode impératif simulé : l'affichage ne se met plus à jour tout seul."
                } else {
                    "Mode déclaratif : l'affichage suit l'état, sans un seul setText."
                },
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val valeurAffichee = if (modeImperatif) affichageManuel else compteur
                Text(
                    text = "Compteur affiché : $valeurAffichee",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "État réel : $compteur",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { compteur++ }) { Text("Incrémenter l'état") }
                    if (modeImperatif) {
                        FilledTonalButton(onClick = { affichageManuel = compteur }) {
                            Text("Rafraîchir l'affichage")
                        }
                    }
                }
                if (modeImperatif && affichageManuel != compteur) {
                    Text(
                        text = "L'affichage est désynchronisé de l'état. C'est le bug visuel " +
                            "quotidien du monde XML : un setText oublié. Compose supprime " +
                            "cette classe de bugs par construction.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        TitreSection(emoji = "📊", texte = "La recomposition, mesurée")
        Text(
            text = "Chaque bloc compte ses propres recompositions. Incrémentez le compteur " +
                "et observez : seuls les blocs qui lisent l'état modifié sont rejoués.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BlocMesure(
                nom = "Lit le compteur",
                detail = "Ce bloc affiche `$compteur`, donc il dépend de l'état.",
            )
            BlocMesureIndependant()
        }
    }
}

@Composable
private fun BlocMesure(nom: String, detail: String) {
    val recompositions = compteurRecompositions()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = nom, style = MaterialTheme.typography.titleSmall)
                EtiquetteInfo("$recompositions recomposition(s)")
            }
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

/**
 * Ce bloc ne lit aucun état mutable : son compteur reste à 1 quoi qu'il arrive.
 * C'est la démonstration du périmètre de recomposition.
 */
@Composable
private fun BlocMesureIndependant() {
    val recompositions = compteurRecompositions()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Ne lit aucun état", style = MaterialTheme.typography.titleSmall)
                EtiquetteInfo("$recompositions recomposition(s)")
            }
            Text(
                text = "Ce bloc reste à 1 : Compose ne le rejoue pas, puisque rien de ce " +
                    "qu'il lit n'a changé. La recomposition est ciblée, pas globale.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview(name = "2.1 clair", showBackground = true)
@Composable
private fun ApercuLecon21Clair() {
    ShowcaseTheme(sombre = false) { DemoParadigmeDeclaratif() }
}

@Preview(name = "2.1 sombre", showBackground = true)
@Composable
private fun ApercuLecon21Sombre() {
    ShowcaseTheme(sombre = true) { DemoParadigmeDeclaratif() }
}
