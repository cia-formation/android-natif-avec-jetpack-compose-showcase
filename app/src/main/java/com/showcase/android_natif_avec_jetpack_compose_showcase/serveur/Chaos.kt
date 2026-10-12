package com.showcase.android_natif_avec_jetpack_compose_showcase.serveur

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Réglages de perturbation du serveur embarqué. La leçon 6.3 (erreurs, retry,
 * offline-first) les pilote depuis sa démo : on provoque de vraies pannes HTTP et
 * de vraies latences, au lieu de les simuler côté client.
 */
data class ReglagesChaos(
    /** Probabilité qu'une requête échoue, entre 0f et 1f. */
    val tauxEchec: Float = 0f,
    /** Latence artificielle ajoutée avant chaque réponse. */
    val latenceMs: Long = 0L,
    /** Code HTTP renvoyé quand une panne est déclenchée. */
    val codeEchec: Int = 503,
    /** Coupe toute réponse : simule un serveur injoignable. */
    val horsService: Boolean = false,
)

@Singleton
class PiloteChaos @Inject constructor() {

    private val _reglages = MutableStateFlow(ReglagesChaos())
    val reglages: StateFlow<ReglagesChaos> = _reglages

    /** Compteur d'appels servis et de pannes injectées, affiché par les démos. */
    private val _statistiques = MutableStateFlow(StatistiquesChaos())
    val statistiques: StateFlow<StatistiquesChaos> = _statistiques

    fun definirTauxEchec(taux: Float) = _reglages.update { it.copy(tauxEchec = taux.coerceIn(0f, 1f)) }

    fun definirLatence(ms: Long) = _reglages.update { it.copy(latenceMs = ms.coerceAtLeast(0L)) }

    fun definirCodeEchec(code: Int) = _reglages.update { it.copy(codeEchec = code) }

    fun definirHorsService(horsService: Boolean) = _reglages.update { it.copy(horsService = horsService) }

    fun reinitialiser() {
        _reglages.value = ReglagesChaos()
        _statistiques.value = StatistiquesChaos()
    }

    internal fun enregistrerSucces() = _statistiques.update { it.copy(servis = it.servis + 1) }

    internal fun enregistrerEchec() = _statistiques.update { it.copy(echoues = it.echoues + 1) }
}

data class StatistiquesChaos(
    val servis: Int = 0,
    val echoues: Int = 0,
)
