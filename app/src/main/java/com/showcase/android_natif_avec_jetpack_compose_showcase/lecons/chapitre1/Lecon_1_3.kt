package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre1

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.BuildConfig
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.LigneValeur
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_1_3 = SpecLecon(
    chapitre = 1,
    numero = 3,
    titre = "Créer et explorer un projet Compose",
    resume = "Générer un projet Empty Compose Activity, lire son arborescence (manifest, " +
        "Gradle, res, MainActivity) et lancer l'application sur émulateur puis sur " +
        "appareil physique via ADB.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Générer un projet Empty Compose Activity",
        "Décrire l'arborescence : manifest, scripts Gradle, ressources, MainActivity",
        "Identifier le rôle de chaque fichier généré par le modèle",
        "Lancer le projet sur émulateur, puis sur appareil physique via ADB",
        "Versionner le projet dès le premier commit",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Le point d'entrée généré par le modèle",
            code = """
                class MainActivity : ComponentActivity() {
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        enableEdgeToEdge()
                        setContent {
                            MonTheme {
                                Scaffold(modifier = Modifier.fillMaxSize()) { marges ->
                                    Greeting(name = "Android", modifier = Modifier.padding(marges))
                                }
                            }
                        }
                    }
                }
            """.trimIndent(),
            commentaire = "setContent est la frontière : au-dessus, le monde Android " +
                "classique (Activity, Bundle) ; en dessous, Compose.",
        ),
        ExtraitCode(
            titre = "Le MainActivity de ce showcase",
            code = """
                @AndroidEntryPoint
                class MainActivity : ComponentActivity() {
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        enableEdgeToEdge()
                        setContent {
                            ShowcaseTheme {
                                Surface(modifier = Modifier.fillMaxSize()) {
                                    NavigationCatalogue()
                                }
                            }
                        }
                    }
                }
            """.trimIndent(),
            commentaire = "Même squelette, deux ajouts : @AndroidEntryPoint pour Hilt " +
                "(leçon 4.5) et une navigation Compose à la place du Greeting (chapitre 3).",
        ),
        ExtraitCode(
            titre = "Déployer sur un appareil physique",
            code = """
                # 1. Vérifier que l'appareil est visible et autorisé
                adb devices -l

                # 2. Installer la variante debug
                ./gradlew :app:installDebug

                # 3. Lancer sans toucher au téléphone
                adb shell am start -n com.exemple.monapp/.MainActivity

                # 4. Suivre les logs de l'app uniquement
                adb logcat --pid=${'$'}(adb shell pidof -s com.exemple.monapp)
            """.trimIndent(),
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "Modifier le mauvais build.gradle.kts",
            cause = "Il y en a deux : celui de la racine et celui du module app.",
            remede = "Les dépendances vont dans app/build.gradle.kts. La racine ne déclare " +
                "que les plugins, en général avec apply false.",
        ),
        Piege(
            probleme = "Commiter local.properties",
            cause = "Le fichier est généré automatiquement et ressemble à de la config utile.",
            remede = "Il contient le chemin du SDK, propre à chaque machine. Il doit rester " +
                "dans .gitignore — comme ici.",
        ),
        Piege(
            probleme = "« INSTALL_FAILED_UPDATE_INCOMPATIBLE »",
            cause = "Une version signée avec une autre clé est déjà installée.",
            remede = "adb uninstall com.exemple.monapp avant de réinstaller.",
        ),
    ),
    aRetenir = listOf(
        "Un projet Android a deux scripts Gradle : la racine pour les plugins, le module pour les dépendances",
        "setContent est la frontière entre le monde Activity et le monde Compose",
        "AndroidManifest.xml déclare les composants, les permissions et le point d'entrée",
        "res/ contient les ressources ; Compose n'en supprime pas le besoin (icônes, chaînes, thème XML de lancement)",
        "installDebug puis am start permettent de déployer et lancer sans quitter le terminal",
    ),
    demo = { DemoArborescence() },
)

/** Un nœud de l'arborescence d'un projet Compose, avec son rôle expliqué. */
private data class NoeudProjet(
    val chemin: String,
    val niveau: Int,
    val role: String,
    val dossier: Boolean = false,
)

private val ARBORESCENCE = listOf(
    NoeudProjet("settings.gradle.kts", 0, "Déclare les modules du projet et les dépôts de plugins. Lu en premier par Gradle."),
    NoeudProjet("build.gradle.kts", 0, "Script racine : déclare les plugins pour tout le projet, en général avec apply false."),
    NoeudProjet("gradle/libs.versions.toml", 0, "Catalogue de versions centralisé. Une seule source de vérité pour les dépendances."),
    NoeudProjet("gradle.properties", 0, "Réglages du build : mémoire du daemon, cache de configuration, style de code Kotlin."),
    NoeudProjet("local.properties", 0, "Chemin du SDK, propre à votre machine. Jamais commité."),
    NoeudProjet("gradlew", 0, "Wrapper Gradle : garantit que tout le monde build avec la même version de Gradle."),
    NoeudProjet("app/", 0, "Le module applicatif. Un projet peut en contenir plusieurs.", dossier = true),
    NoeudProjet("app/build.gradle.kts", 1, "Configuration du module : SDK cibles, dépendances, variantes de build."),
    NoeudProjet("app/src/main/AndroidManifest.xml", 1, "Déclare l'Application, les Activities, les permissions et les intent-filters."),
    NoeudProjet("app/src/main/java/.../MainActivity.kt", 1, "Point d'entrée. setContent y installe l'arbre Compose."),
    NoeudProjet("app/src/main/java/.../ui/theme/", 1, "Couleurs, typographie et formes du thème Material 3.", dossier = true),
    NoeudProjet("app/src/main/res/", 1, "Ressources : icônes, chaînes, thème XML de lancement, configurations réseau.", dossier = true),
    NoeudProjet("app/src/test/", 1, "Tests JVM, exécutés sans appareil. Rapides.", dossier = true),
    NoeudProjet("app/src/androidTest/", 1, "Tests instrumentés, exécutés sur appareil ou émulateur.", dossier = true),
)

@Composable
private fun DemoArborescence() {
    val ouverts = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "📁", texte = "L'arborescence, fichier par fichier")
        Text(
            text = "Touchez un fichier pour lire son rôle. C'est l'arborescence réelle de " +
                "ce showcase.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            ARBORESCENCE.forEach { noeud ->
                NoeudLigne(
                    noeud = noeud,
                    ouvert = ouverts[noeud.chemin] == true,
                    surClic = { ouverts[noeud.chemin] = ouverts[noeud.chemin] != true },
                )
            }
        }

        TitreSection(emoji = "🏷️", texte = "L'application installée")
        Column {
            LigneValeur("applicationId", BuildConfig.APPLICATION_ID)
            LigneValeur("versionName", BuildConfig.VERSION_NAME)
            LigneValeur("versionCode", BuildConfig.VERSION_CODE.toString())
            LigneValeur("Variante", BuildConfig.BUILD_TYPE)
            LigneValeur("Débogable", BuildConfig.DEBUG.toString())
        }
        Text(
            text = "Ces valeurs viennent de BuildConfig, une classe générée par l'AGP à " +
                "partir de app/build.gradle.kts. C'est le pont le plus simple entre le " +
                "build et le code.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NoeudLigne(noeud: NoeudProjet, ouvert: Boolean, surClic: () -> Unit) {
    Card(
        onClick = surClic,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ouvert) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            },
        ),
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "  ".repeat(noeud.niveau) + if (noeud.dossier) "📂" else "📄",
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = noeud.chemin,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            AnimatedVisibility(visible = ouvert) {
                Text(
                    text = noeud.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                )
            }
        }
    }
}

@Preview(name = "1.3 clair", showBackground = true)
@Composable
private fun ApercuLecon13Clair() {
    ShowcaseTheme(sombre = false) { DemoArborescence() }
}

@Preview(name = "1.3 sombre", showBackground = true)
@Composable
private fun ApercuLecon13Sombre() {
    ShowcaseTheme(sombre = true) { DemoArborescence() }
}
