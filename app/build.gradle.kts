import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

/**
 * Lit une propriété de configuration locale. `local.defaults.properties` est commité avec
 * des valeurs vides et sert de documentation ; `local.properties` (ignoré par git) la
 * surcharge avec les vraies clés de l'apprenant. Aucune clé ne part donc dans l'historique.
 */
fun proprieteLocale(cle: String): String {
    val valeurs = Properties()
    listOf("local.defaults.properties", "local.properties").forEach { nom ->
        val fichier = rootProject.file(nom)
        if (fichier.exists()) fichier.inputStream().use(valeurs::load)
    }
    return valeurs.getProperty(cle).orEmpty()
}

// Firebase n'est câblé que si l'apprenant a déposé son propre google-services.json
// (cf. leçon 6.4). Sans ce fichier, le plugin n'est pas appliqué et la démo affiche
// sa checklist de configuration au lieu de faire échouer le build de tout le monde.
val fichierGoogleServices = file("google-services.json")
val firebaseActif = fichierGoogleServices.exists()
if (firebaseActif) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
}

android {
    namespace = "com.showcase.android_natif_avec_jetpack_compose_showcase"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.showcase.android_natif_avec_jetpack_compose_showcase"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Identifiants Supabase (leçon 6.5), vides par défaut : voir local.defaults.properties.
        buildConfigField("String", "SUPABASE_URL", "\"${proprieteLocale("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${proprieteLocale("SUPABASE_ANON_KEY")}\"")
        buildConfigField("boolean", "FIREBASE_ACTIF", "$firebaseActif")

        // Introspection de la chaine de build, lue par les demos du chapitre 1 :
        // les lecons 1.1 a 1.4 affichent les vraies valeurs de CE build plutot que
        // des exemples recopies. Une lecon ne peut pas modifier le build (cf. CONTRIBUTING),
        // donc c'est le socle qui expose ces valeurs.
        buildConfigField("int", "SDK_COMPILE", "37")
        buildConfigField("int", "SDK_TARGET", "37")
        buildConfigField("int", "SDK_MIN", "28")
        buildConfigField("String", "VERSION_AGP", "\"${libs.versions.agp.get()}\"")
        buildConfigField("String", "VERSION_KOTLIN", "\"${libs.versions.kotlin.get()}\"")
        buildConfigField("String", "VERSION_KSP", "\"${libs.versions.ksp.get()}\"")
        buildConfigField("String", "VERSION_HILT", "\"${libs.versions.hilt.get()}\"")
        buildConfigField("String", "VERSION_COMPOSE_BOM", "\"${libs.versions.composeBom.get()}\"")
        buildConfigField("String", "VERSION_GRADLE", "\"${gradle.gradleVersion}\"")
        buildConfigField("String", "VERSION_JDK", "\"${JavaVersion.current()}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            // Ktor serveur + client embarquent des métadonnées en double.
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }
}

dependencies {
    // --- Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)

    // --- Cycle de vie et ViewModels (chapitre 4)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- Navigation (chapitre 3)
    implementation(libs.androidx.navigation.compose)

    // --- Injection de dépendances (leçon 4.5)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // --- Persistance (chapitre 5)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.security.crypto)

    // --- Réseau client (chapitre 6)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.serialization.json)

    // --- Serveur embarqué : backend local des chapitres 6 et 7
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.websockets)

    // --- Sérialisation, coroutines, images
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // --- BaaS (leçons 6.4 et 6.5)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)

    // --- Tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// ---------------------------------------------------------------------------
// Registre des leçons, généré au build
// ---------------------------------------------------------------------------
// Un registre écrit à la main serait modifié par les 40 branches de leçons, donc
// 40 conflits au merge. En le générant depuis les fichiers présents, une branche de
// leçon n'ajoute qu'un fichier Kotlin et le catalogue le découvre tout seul.

abstract class GenererRegistreLecons : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourcesLecons: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val sortie: DirectoryProperty

    @TaskAction
    fun generer() {
        val regexPaquet = Regex("""^package\s+([\w.]+)""", RegexOption.MULTILINE)
        val regexVal = Regex("""^val\s+(lecon_(\d+)_(\d+))\b""", RegexOption.MULTILINE)

        val references = sourcesLecons.files
            .filter { it.isFile }
            .mapNotNull { fichier ->
                val texte = fichier.readText()
                val paquet = regexPaquet.find(texte)?.groupValues?.get(1) ?: return@mapNotNull null
                val trouve = regexVal.find(texte) ?: return@mapNotNull null
                Triple(
                    "$paquet.${trouve.groupValues[1]}",
                    trouve.groupValues[2].toInt(),
                    trouve.groupValues[3].toInt(),
                )
            }
            .sortedWith(compareBy({ it.second }, { it.third }))

        val dossier = sortie.get().asFile
            .resolve("com/showcase/android_natif_avec_jetpack_compose_showcase/catalogue")
        dossier.mkdirs()

        val lignes = references.joinToString("\n") { "    ${it.first}," }
        dossier.resolve("RegistreLecons.kt").writeText(
            """
            |// Fichier généré par la tâche Gradle `genererRegistreLecons`. Ne pas éditer, ne pas commiter.
            |// Source : app/src/main/java/.../lecons/chapitre*/Lecon_*.kt
            |package com.showcase.android_natif_avec_jetpack_compose_showcase.catalogue
            |
            |import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
            |
            |/** Les ${references.size} leçon(s) dont la démo est présente sur la branche courante. */
            |val LECONS_ENREGISTREES: List<SpecLecon> = listOf(
            |$lignes
            |)
            |
            """.trimMargin(),
        )
        logger.lifecycle("Registre des leçons : ${references.size} démo(s) enregistrée(s).")
    }
}

val tacheRegistreLecons = tasks.register<GenererRegistreLecons>("genererRegistreLecons") {
    description = "Génère RegistreLecons.kt depuis les fichiers Lecon_*.kt présents."
    group = "showcase"
    sourcesLecons.from(
        fileTree("src/main/java/com/showcase/android_natif_avec_jetpack_compose_showcase/lecons") {
            include("**/Lecon_*.kt")
        },
    )
}

androidComponents {
    onVariants { variante ->
        variante.sources.kotlin?.addGeneratedSourceDirectory(
            tacheRegistreLecons,
            GenererRegistreLecons::sortie,
        )
    }
}


// ---------------------------------------------------------------------------
// Fichiers de build embarques dans les assets
// ---------------------------------------------------------------------------
// La lecon 1.4 enseigne la lecture d'un build.gradle.kts. Plutot que de recopier
// un exemple qui divergerait, on embarque les vrais fichiers du projet dans l'APK.

abstract class GenererSourcesBuild : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val fichiersBuild: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val sortie: DirectoryProperty

    @TaskAction
    fun copier() {
        val dossier = sortie.get().asFile.resolve("build")
        dossier.mkdirs()
        // Les noms publies sont stables : ils sont declares par socle/SourcesBuild.kt.
        val renommages = mapOf(
            "build.gradle.kts" to "app-build.gradle.kts",
        )
        fichiersBuild.files.filter { it.isFile }.forEach { fichier ->
            val nom = renommages[fichier.name] ?: fichier.name
            dossier.resolve(nom).writeText(fichier.readText())
        }
    }
}

val tacheSourcesBuild = tasks.register<GenererSourcesBuild>("genererSourcesBuild") {
    description = "Embarque les vrais fichiers de build du projet dans les assets (lecon 1.4)."
    group = "showcase"
    fichiersBuild.from(
        layout.projectDirectory.file("build.gradle.kts"),
        rootProject.layout.projectDirectory.file("settings.gradle.kts"),
        rootProject.layout.projectDirectory.file("gradle/libs.versions.toml"),
        rootProject.layout.projectDirectory.file("gradle/wrapper/gradle-wrapper.properties"),
    )
}

androidComponents {
    onVariants { variante ->
        variante.sources.assets?.addGeneratedSourceDirectory(
            tacheSourcesBuild,
            GenererSourcesBuild::sortie,
        )
    }
}
