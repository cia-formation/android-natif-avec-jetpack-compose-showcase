package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre1

import android.os.Build
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.BuildConfig
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtiquetteInfo
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.LigneValeur
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_1_1 = SpecLecon(
    chapitre = 1,
    numero = 1,
    titre = "L'écosystème Android moderne",
    resume = "Situer Android Studio, le SDK, Gradle et Jetpack dans la chaîne d'outils " +
        "moderne, puis identifier les versions cibles (min, target, compile SDK) d'un projet.",
    dureeMinutes = 60,
    objectifs = listOf(
        "Situer Android Studio, le SDK Android, Gradle et Jetpack dans la chaîne de production",
        "Expliquer le rôle de Jetpack Compose face à l'ancien système de vues XML",
        "Identifier les versions cibles d'un projet : minSdk, targetSdk, compileSdk",
        "Distinguer Android Gradle Plugin, compilateur Kotlin et plugin Compose",
        "Décrire le cycle : code source → APK → installation sur un appareil",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Les trois versions du SDK",
            code = """
                android {
                    compileSdk = 37      // APIs contre lesquelles on compile

                    defaultConfig {
                        minSdk = 28      // version Android minimale d'installation
                        targetSdk = 37   // version testée et optimisée
                    }
                }
            """.trimIndent(),
            commentaire = "Les confondre est l'erreur classique du débutant : compileSdk " +
                "concerne le compilateur, minSdk l'installation, targetSdk le comportement " +
                "de la plateforme à l'exécution.",
        ),
        ExtraitCode(
            titre = "Le même projet, en vrai",
            code = """
                // Ce showcase utilise la syntaxe AGP 9, où compileSdk est un bloc :
                compileSdk {
                    version = release(37)
                }
            """.trimIndent(),
            commentaire = "La démo ci-dessus lit ces valeurs dans BuildConfig et les compare " +
                "à l'appareil sur lequel vous lisez cette page.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "Confondre compileSdk et targetSdk",
            cause = "Les deux valent souvent le même nombre, donc la distinction ne se voit pas.",
            remede = "compileSdk = ce que le compilateur connaît ; targetSdk = ce que la " +
                "plateforme applique à l'exécution. Monter compileSdk ne change rien au " +
                "comportement, monter targetSdk, oui.",
        ),
        Piege(
            probleme = "Descendre minSdk « au cas où »",
            cause = "Vouloir couvrir le maximum d'appareils.",
            remede = "Chaque palier descendu ajoute des branches de compatibilité à écrire " +
                "et à tester. Regardez la part de marché réelle avant de descendre.",
        ),
        Piege(
            probleme = "Croire que Compose remplace la plateforme Android",
            cause = "Le changement de paradigme est spectaculaire côté UI.",
            remede = "Compose ne remplace que la couche de description de l'écran. Cycle de " +
                "vie, permissions et services système restent identiques.",
        ),
    ),
    aRetenir = listOf(
        "Android Studio orchestre, Gradle construit, l'AGP ajoute les tâches Android, le SDK fournit les APIs",
        "Compose remplace XML pour décrire l'UI, sans changer la plateforme sous-jacente",
        "compileSdk compile, minSdk conditionne l'installation, targetSdk conditionne le comportement",
        "Le cycle est toujours le même : sources Kotlin → Gradle + AGP → APK/AAB → appareil",
        "Toute nouvelle application devrait démarrer Compose-first",
    ),
    demo = { DemoEcosysteme() },
)

/** Une étape de la chaîne de production Android. */
private data class EtapeChaine(
    val nom: String,
    val role: String,
    val detail: String,
)

private val CHAINE = listOf(
    EtapeChaine(
        nom = "Sources Kotlin",
        role = "Ce que vous écrivez",
        detail = "Des fonctions @Composable, des ViewModels, des classes de données. " +
            "Rien d'exécutable encore : du texte dans des fichiers .kt.",
    ),
    EtapeChaine(
        nom = "Android Studio",
        role = "L'atelier",
        detail = "IDE basé sur IntelliJ. Il édite, déclenche le build, déploie, profile. " +
            "Il ne compile rien lui-même : il pilote Gradle.",
    ),
    EtapeChaine(
        nom = "Gradle + AGP",
        role = "Le moteur de build",
        detail = "Gradle lit les scripts build.gradle.kts et télécharge les dépendances. " +
            "L'Android Gradle Plugin y greffe les tâches Android : ressources, " +
            "signature, packaging.",
    ),
    EtapeChaine(
        nom = "SDK Android",
        role = "Les APIs et les outils",
        detail = "Bibliothèques de plateforme pour chaque version d'Android, plus les " +
            "outils ligne de commande : adb, emulator, aapt2, d8.",
    ),
    EtapeChaine(
        nom = "APK / AAB",
        role = "Le binaire",
        detail = "L'APK s'installe directement ; l'AAB est le format soumis au Play Store, " +
            "qui en dérive un APK optimisé par appareil.",
    ),
    EtapeChaine(
        nom = "Appareil",
        role = "L'exécution",
        detail = "Émulateur ou téléphone physique. C'est ici que tourne l'application " +
            "que vous lisez en ce moment.",
    ),
)

@Composable
private fun DemoEcosysteme() {
    var selection by remember { mutableStateOf(CHAINE.first()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "🔗", texte = "La chaîne de production")
        Text(
            text = "Touchez une étape pour voir son rôle.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            CHAINE.forEachIndexed { index, etape ->
                EtapeChaineLigne(
                    index = index,
                    etape = etape,
                    selectionnee = etape == selection,
                    surClic = { selection = etape },
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = selection.nom, style = MaterialTheme.typography.titleSmall)
                Text(text = selection.role, style = MaterialTheme.typography.labelMedium)
                Text(text = selection.detail, style = MaterialTheme.typography.bodySmall)
            }
        }

        TitreSection(emoji = "🎯", texte = "Les trois SDK, ici et maintenant")
        Column {
            LigneValeur("compileSdk (compilation)", BuildConfig.SDK_COMPILE.toString())
            LigneValeur("targetSdk (comportement)", BuildConfig.SDK_TARGET.toString())
            LigneValeur("minSdk (installation)", BuildConfig.SDK_MIN.toString())
            LigneValeur("Cet appareil (Build.VERSION.SDK_INT)", Build.VERSION.SDK_INT.toString())
            LigneValeur("Version Android", "Android ${Build.VERSION.RELEASE}")
        }
        VerdictCompatibilite()

        TitreSection(emoji = "🧰", texte = "Les versions de la chaîne")
        Column {
            LigneValeur("Android Gradle Plugin", BuildConfig.VERSION_AGP)
            LigneValeur("Kotlin (plugin Compose)", BuildConfig.VERSION_KOTLIN)
            LigneValeur("BOM Compose", BuildConfig.VERSION_COMPOSE_BOM)
            LigneValeur("Gradle", BuildConfig.VERSION_GRADLE)
        }
    }
}

@Composable
private fun EtapeChaineLigne(
    index: Int,
    etape: EtapeChaine,
    selectionnee: Boolean,
    surClic: () -> Unit,
) {
    val fond by animateColorAsState(
        targetValue = if (selectionnee) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
        label = "fond-etape",
    )
    Card(
        onClick = surClic,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = fond),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${index + 1}",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = etape.nom,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            if (index < CHAINE.lastIndex) {
                Text(
                    text = "↓",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun VerdictCompatibilite() {
    val actuel = Build.VERSION.SDK_INT
    val compatible = actuel >= BuildConfig.SDK_MIN
    val message = if (compatible) {
        "Cet appareil (API $actuel) est au-dessus de minSdk ${BuildConfig.SDK_MIN} : " +
            "l'application s'installe. Si minSdk valait ${actuel + 1}, le Play Store ne " +
            "proposerait même pas cette app sur cet appareil."
    } else {
        "Cet appareil (API $actuel) est sous minSdk ${BuildConfig.SDK_MIN} — cas " +
            "théoriquement impossible, puisque l'app tourne."
    }
    Surface(
        color = if (compatible) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.errorContainer
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EtiquetteInfo(if (compatible) "compatible" else "incompatible")
            Text(text = message, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(name = "1.1 clair", showBackground = true)
@Composable
private fun ApercuLecon11Clair() {
    ShowcaseTheme(sombre = false) { DemoEcosysteme() }
}

@Preview(name = "1.1 sombre", showBackground = true)
@Composable
private fun ApercuLecon11Sombre() {
    ShowcaseTheme(sombre = true) { DemoEcosysteme() }
}
