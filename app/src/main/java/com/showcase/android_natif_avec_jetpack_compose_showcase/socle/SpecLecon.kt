package com.showcase.android_natif_avec_jetpack_compose_showcase.socle

import androidx.compose.runtime.Composable

/**
 * Contrat d'une lecon du showcase. Chaque fichier `Lecon_C_L.kt` expose exactement
 * un `val lecon_C_L: SpecLecon` top-level, que la tache Gradle `genererRegistreLecons`
 * decouvre pour alimenter le catalogue.
 */
data class SpecLecon(
    val chapitre: Int,
    val numero: Int,
    val titre: String,
    val resume: String,
    val dureeMinutes: Int,
    val objectifs: List<String> = emptyList(),
    val extraits: List<ExtraitCode> = emptyList(),
    val pieges: List<Piege> = emptyList(),
    val aRetenir: List<String> = emptyList(),
    /** true : la demo occupe tout l'ecran (mini-app, navigation imbriquee, HabitFlow). */
    val pleinEcran: Boolean = false,
    val demo: @Composable () -> Unit,
) {
    val identifiant: String get() = "$chapitre.$numero"
}

/** Un extrait de code commente, repris de la lecon Markdown source. */
data class ExtraitCode(
    val titre: String,
    val code: String,
    val commentaire: String? = null,
)

/** Une ligne du tableau "Pieges frequents" de la lecon. */
data class Piege(
    val probleme: String,
    val cause: String,
    val remede: String,
)
