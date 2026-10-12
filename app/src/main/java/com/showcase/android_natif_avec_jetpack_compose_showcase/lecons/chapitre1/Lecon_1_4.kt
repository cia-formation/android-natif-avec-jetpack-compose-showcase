package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.BuildConfig
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.BlocCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.LigneValeur
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SourcesBuild
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_1_4 = SpecLecon(
    chapitre = 1,
    numero = 4,
    titre = "Le cycle Gradle et les dépendances Jetpack",
    resume = "Lire un build.gradle.kts Android, ajouter une dépendance Jetpack via le BOM " +
        "Compose et diagnostiquer les erreurs de build Gradle les plus fréquentes.",
    dureeMinutes = 60,
    objectifs = listOf(
        "Lire un build.gradle.kts Android et savoir quoi y chercher",
        "Comprendre le rôle d'un catalogue de versions (libs.versions.toml)",
        "Ajouter une dépendance Jetpack depuis le BOM Compose",
        "Distinguer les configurations implementation, api, ksp et testImplementation",
        "Diagnostiquer les erreurs de build Gradle les plus fréquentes",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Le BOM Compose : une version pour tout un ensemble",
            code = """
                dependencies {
                    // Le BOM ne contient aucun code : il fixe les versions cohérentes
                    // de toutes les bibliothèques Compose.
                    implementation(platform("androidx.compose:compose-bom:2026.02.01"))

                    // Pas de version ici : le BOM la fournit.
                    implementation("androidx.compose.material3:material3")
                    implementation("androidx.compose.ui:ui")
                }
            """.trimIndent(),
            commentaire = "C'est pourquoi le catalogue de ce projet déclare material3 et ui " +
                "sans version : le BOM est la source de vérité.",
        ),
        ExtraitCode(
            titre = "Les configurations à ne pas confondre",
            code = """
                dependencies {
                    implementation(libs.retrofit)        // dans l'app, pas exposé aux modules dépendants
                    api(libs.okhttp)                     // exposé transitivement (modules de bibliothèque)
                    ksp(libs.androidx.room.compiler)     // génération de code à la compilation
                    testImplementation(libs.junit)       // uniquement pour les tests JVM
                    debugImplementation(libs.androidx.compose.ui.tooling)  // uniquement en debug
                }
            """.trimIndent(),
            commentaire = "ksp remplace kapt : avec le Kotlin intégré d'AGP 9, kapt n'est " +
                "plus utilisable du tout.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "« Could not find com.exemple:lib:1.0 »",
            cause = "Le dépôt qui héberge l'artefact n'est pas déclaré, ou la coordonnée est fautive.",
            remede = "Vérifier dependencyResolutionManagement dans settings.gradle.kts. Ce " +
                "projet n'autorise que google() et mavenCentral().",
        ),
        Piege(
            probleme = "« Duplicate class » au moment du dexing",
            cause = "Deux dépendances embarquent la même classe, souvent via des versions transitives divergentes.",
            remede = "./gradlew :app:dependencies pour voir l'arbre, puis aligner les " +
                "versions — c'est précisément le problème que résout un BOM.",
        ),
        Piege(
            probleme = "Ajouter une version à une dépendance gérée par le BOM",
            cause = "Réflexe pris sur des projets sans BOM.",
            remede = "Une version explicite écrase celle du BOM et casse la cohérence de " +
                "l'ensemble Compose. Laissez la version vide.",
        ),
        Piege(
            probleme = "« kapt is not compatible with built-in Kotlin »",
            cause = "Un tutoriel antérieur à AGP 9 qui utilise encore kapt.",
            remede = "Remplacer le plugin kapt par ksp et chaque kapt(...) par ksp(...).",
        ),
    ),
    aRetenir = listOf(
        "Le script racine déclare les plugins, le script du module déclare les dépendances",
        "Un catalogue de versions (libs.versions.toml) centralise toutes les coordonnées",
        "Un BOM ne contient pas de code : il fixe un ensemble de versions cohérentes",
        "implementation, api, ksp, testImplementation et debugImplementation ne ciblent pas la même chose",
        "Avec le Kotlin intégré d'AGP 9, toute génération de code passe par ksp",
    ),
    demo = { DemoGradle() },
)

private val ONGLETS = listOf(
    "app/build.gradle.kts" to SourcesBuild.BUILD_APP,
    "libs.versions.toml" to SourcesBuild.CATALOGUE_VERSIONS,
    "settings.gradle.kts" to SourcesBuild.REGLAGES,
    "gradle-wrapper" to SourcesBuild.WRAPPER,
)

@Composable
private fun DemoGradle() {
    val contexte = LocalContext.current
    var ongletActif by remember { mutableIntStateOf(0) }

    val (nom, chemin) = ONGLETS[ongletActif]
    val contenu = remember(chemin) {
        runCatching {
            contexte.assets.open(chemin).bufferedReader().use { it.readText() }
        }.getOrElse { erreur ->
            "// Fichier indisponible : ${erreur.message}\n" +
                "// Relancez ./gradlew :app:genererSourcesBuild"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TitreSection(emoji = "📜", texte = "Les vrais fichiers de build de ce projet")
        Text(
            text = "Ce ne sont pas des exemples : ces fichiers sont embarqués dans l'APK " +
                "au moment du build, par la tâche Gradle genererSourcesBuild.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        PrimaryTabRow(selectedTabIndex = ongletActif) {
            ONGLETS.forEachIndexed { index, (titre, _) ->
                Tab(
                    selected = index == ongletActif,
                    onClick = { ongletActif = index },
                    text = {
                        Text(
                            text = titre,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    },
                )
            }
        }

        BlocCode(
            extrait = ExtraitCode(
                titre = nom,
                code = contenu,
            ),
            modifier = Modifier.padding(top = 4.dp),
        )

        TitreSection(emoji = "📦", texte = "Ce que le BOM Compose pilote ici")
        Column {
            LigneValeur("BOM Compose", BuildConfig.VERSION_COMPOSE_BOM)
            LigneValeur("material3", "version fournie par le BOM")
            LigneValeur("compose.ui", "version fournie par le BOM")
            LigneValeur("material-icons-extended", "version fournie par le BOM")
        }
        Text(
            text = "Cherchez material3 dans l'onglet libs.versions.toml : la ligne n'a pas " +
                "de version.ref. C'est volontaire.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "1.4 clair", showBackground = true)
@Composable
private fun ApercuLecon14Clair() {
    ShowcaseTheme(sombre = false) { DemoGradle() }
}

@Preview(name = "1.4 sombre", showBackground = true)
@Composable
private fun ApercuLecon14Sombre() {
    ShowcaseTheme(sombre = true) { DemoGradle() }
}
