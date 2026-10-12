package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre1

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
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

val lecon_1_2 = SpecLecon(
    chapitre = 1,
    numero = 2,
    titre = "Installation et configuration d'Android Studio",
    resume = "Installer Android Studio sur Windows, macOS ou Linux, configurer le SDK " +
        "Manager et le JDK, puis créer un premier émulateur AVD fonctionnel.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Installer Android Studio sur Windows, macOS ou Linux",
        "Configurer le SDK Manager et choisir les plateformes à télécharger",
        "Vérifier le JDK utilisé par le build",
        "Créer un émulateur AVD fonctionnel",
        "Distinguer un émulateur d'un appareil physique à l'exécution",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Vérifier l'atelier en ligne de commande",
            code = """
                # Les trois commandes qui confirment une installation saine
                adb devices -l                      # appareils et émulateurs visibles
                sdkmanager --list_installed          # plateformes et outils installés
                emulator -list-avds                  # émulateurs déjà créés
            """.trimIndent(),
            commentaire = "Ces binaires vivent dans le SDK : platform-tools/adb, " +
                "cmdline-tools/latest/bin/sdkmanager, emulator/emulator.",
        ),
        ExtraitCode(
            titre = "Lire l'appareil depuis le code",
            code = """
                // Ce que la démo affiche, à la source
                val modele = "${'$'}{Build.MANUFACTURER} ${'$'}{Build.MODEL}"
                val api = Build.VERSION.SDK_INT
                val abis = Build.SUPPORTED_ABIS.joinToString()

                // Heuristique de détection d'émulateur : aucun appareil réel
                // n'expose un fingerprint « generic » ni un modèle « sdk_gphone ».
                val emulateur = Build.FINGERPRINT.startsWith("generic") ||
                    Build.MODEL.contains("sdk_gphone") ||
                    Build.PRODUCT.contains("emulator")
            """.trimIndent(),
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "« adb devices » ne liste pas le téléphone",
            cause = "Débogage USB désactivé, ou autorisation RSA jamais acceptée sur l'écran.",
            remede = "Options développeur → Débogage USB, puis rebrancher et accepter la " +
                "boîte de dialogue qui s'affiche sur le téléphone.",
        ),
        Piege(
            probleme = "L'émulateur rame ou ne démarre pas",
            cause = "Image système d'une architecture différente de celle du processeur.",
            remede = "Sur Apple Silicon, prenez une image arm64-v8a ; sur Intel/AMD, une " +
                "image x86_64 avec l'accélération matérielle activée.",
        ),
        Piege(
            probleme = "Le build utilise un autre JDK que prévu",
            cause = "Android Studio embarque son propre JRE, distinct du JDK du système.",
            remede = "Le JDK du build est celui de la toolchain Gradle, pas celui du PATH. " +
                "Ici, la toolchain est provisionnée automatiquement en JDK 25.",
        ),
    ),
    aRetenir = listOf(
        "Android Studio installe l'IDE ; le SDK Manager installe les plateformes et les outils",
        "adb, sdkmanager et emulator sont les trois binaires qui confirment une installation saine",
        "Choisissez l'image AVD qui correspond à l'architecture de votre machine",
        "Le JDK du build vient de la toolchain Gradle, pas du PATH du système",
        "Build.FINGERPRINT et Build.MODEL permettent de reconnaître un émulateur à l'exécution",
    ),
    demo = { DemoAtelier() },
)

/** Détection d'émulateur : heuristique sur les champs d'identité de la ROM. */
private val estEmulateur: Boolean
    get() = Build.FINGERPRINT.startsWith("generic") ||
        Build.FINGERPRINT.contains("vbox") ||
        Build.MODEL.contains("sdk_gphone") ||
        Build.MODEL.contains("Emulator") ||
        Build.PRODUCT.contains("emulator") ||
        Build.PRODUCT.contains("sdk")

private val ETAPES_INSTALLATION = listOf(
    "Télécharger Android Studio depuis developer.android.com" to
        "La version stable la plus récente ; l'IDE embarque son propre JRE.",
    "SDK Manager → SDK Platforms" to
        "Cochez la plateforme la plus récente, plus celle qui correspond à votre minSdk.",
    "SDK Manager → SDK Tools" to
        "Platform-Tools (adb), Build-Tools, Command-line Tools, Emulator.",
    "Device Manager → créer un AVD" to
        "Choisissez l'image système de l'architecture de votre machine.",
    "Appareil physique : options développeur" to
        "Tapez 7 fois le numéro de build, puis activez le débogage USB.",
    "Vérifier avec adb devices" to
        "L'appareil doit apparaître avec l'état « device », pas « unauthorized ».",
)

@Composable
private fun DemoAtelier() {
    val densite = LocalDensity.current
    // LocalWindowInfo donne la taille reelle du conteneur ; Configuration.screenWidthDp
    // est deconseille depuis Compose 1.9 car ambigu en mode multi-fenetre.
    val tailleConteneur = LocalWindowInfo.current.containerSize
    val largeurDp = with(densite) { tailleConteneur.width.toDp() }
    val hauteurDp = with(densite) { tailleConteneur.height.toDp() }
    val cochees = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TitreSection(emoji = "📱", texte = "L'appareil qui exécute cette page")
        Surface(
            color = if (estEmulateur) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EtiquetteInfo(if (estEmulateur) "émulateur" else "appareil physique")
                Text(
                    text = if (estEmulateur) {
                        "Détecté via Build.FINGERPRINT et Build.MODEL. Un émulateur suffit " +
                            "pour apprendre, mais un vrai téléphone révèle des comportements " +
                            "que l'émulateur masque."
                    } else {
                        "Vous êtes sur du matériel réel : les performances, la densité et le " +
                            "comportement réseau sont ceux de vos utilisateurs."
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Column {
            LigneValeur("Fabricant et modèle", "${Build.MANUFACTURER} ${Build.MODEL}")
            LigneValeur("Android", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            LigneValeur("Architecture", Build.SUPPORTED_ABIS.joinToString())
            LigneValeur("Écran", "${largeurDp.value.toInt()} × ${hauteurDp.value.toInt()} dp")
            LigneValeur("Densité", "${densite.density}× (${(densite.density * 160).toInt()} dpi)")
            LigneValeur("Échelle des polices", "${densite.fontScale}×")
            LigneValeur("Build de la ROM", Build.ID)
        }

        TitreSection(emoji = "🧰", texte = "Ce que ce build utilise")
        Column {
            LigneValeur("Android Gradle Plugin", BuildConfig.VERSION_AGP)
            LigneValeur("Gradle", BuildConfig.VERSION_GRADLE)
            LigneValeur("Toolchain JDK", BuildConfig.VERSION_JDK)
            LigneValeur("Variante installée", BuildConfig.BUILD_TYPE)
        }

        TitreSection(emoji = "✅", texte = "Checklist d'installation")
        Text(
            text = "Cochez au fur et à mesure — l'état n'est pas persisté, c'est un " +
                "aide-mémoire de lecture.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            ETAPES_INSTALLATION.forEach { (etape, detail) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Checkbox(
                        checked = cochees[etape] == true,
                        onCheckedChange = { cochees[etape] = it },
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(text = etape, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "1.2 clair", showBackground = true)
@Composable
private fun ApercuLecon12Clair() {
    ShowcaseTheme(sombre = false) { DemoAtelier() }
}

@Preview(name = "1.2 sombre", showBackground = true)
@Composable
private fun ApercuLecon12Sombre() {
    ShowcaseTheme(sombre = true) { DemoAtelier() }
}
