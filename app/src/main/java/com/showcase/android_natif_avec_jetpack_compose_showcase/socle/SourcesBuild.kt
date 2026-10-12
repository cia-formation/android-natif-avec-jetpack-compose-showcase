package com.showcase.android_natif_avec_jetpack_compose_showcase.socle

/**
 * Chemins, dans les assets de l'APK, des vrais fichiers de build du projet.
 *
 * La tâche Gradle `genererSourcesBuild` les y copie à chaque build. La leçon 1.4 les
 * affiche tels quels : l'apprenant lit le `build.gradle.kts` de l'application qu'il a
 * sous les yeux, pas un exemple recopié qui aurait déjà divergé.
 */
object SourcesBuild {
    const val BUILD_APP = "build/app-build.gradle.kts"
    const val CATALOGUE_VERSIONS = "build/libs.versions.toml"
    const val REGLAGES = "build/settings.gradle.kts"
    const val WRAPPER = "build/gradle-wrapper.properties"
}
