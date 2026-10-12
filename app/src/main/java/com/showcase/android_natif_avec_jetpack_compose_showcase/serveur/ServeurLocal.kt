package com.showcase.android_natif_avec_jetpack_compose_showcase.serveur

import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serveur HTTP embarqué dans l'application, sur la boucle locale.
 *
 * Pourquoi : le cours ne cite qu'une URL fictive (`https://api.habitflow.example`). Pour que
 * les démos réseau des chapitres 6 et 7 soient **réellement vivantes**, déterministes et
 * utilisables hors ligne (mode avion inclus), le showcase embarque son propre backend :
 * REST, JWT et WebSocket, tout en loopback.
 *
 * Le port est choisi par le système (port 0) puis relu via `resolvedConnectors()`, pour ne
 * jamais entrer en conflit avec un autre service de l'appareil.
 */
@Singleton
class ServeurLocal @Inject constructor(
    private val depot: DepotMemoire,
    private val chaos: PiloteChaos,
) {

    private val verrou = Mutex()
    private var moteur: EmbeddedServer<*, *>? = null

    private val _etat = MutableStateFlow<EtatServeur>(EtatServeur.Arrete)
    val etat: StateFlow<EtatServeur> = _etat

    /**
     * Démarre le serveur s'il ne tourne pas déjà et renvoie son URL de base.
     * Idempotent : les six leçons du chapitre 6 peuvent l'appeler sans se coordonner.
     */
    suspend fun demarrerSiNecessaire(): String = verrou.withLock {
        (etat.value as? EtatServeur.Demarre)?.let { return@withLock it.baseUrl }

        _etat.value = EtatServeur.Demarrage
        try {
            withContext(Dispatchers.IO) {
                val serveur = embeddedServer(CIO, port = 0, host = HOTE) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true; prettyPrint = true })
                    }
                    install(WebSockets)
                    configurerRoutes(depot, chaos)
                }
                serveur.start(wait = false)
                val port = serveur.engine.resolvedConnectors().first().port
                moteur = serveur
                _etat.value = EtatServeur.Demarre(baseUrl = "http://$HOTE:$port/")
            }
        } catch (erreur: Exception) {
            _etat.value = EtatServeur.Echec(erreur.message ?: erreur::class.simpleName.orEmpty())
            throw erreur
        }
        (etat.value as EtatServeur.Demarre).baseUrl
    }

    suspend fun arreter() = verrou.withLock {
        withContext(Dispatchers.IO) {
            moteur?.stop(gracePeriodMillis = 200, timeoutMillis = 1_000)
        }
        moteur = null
        _etat.value = EtatServeur.Arrete
    }

    private companion object {
        /** Loopback : la seule origine autorisée en clair par network_security_config.xml. */
        const val HOTE = "127.0.0.1"
    }
}

sealed interface EtatServeur {
    data object Arrete : EtatServeur
    data object Demarrage : EtatServeur
    data class Demarre(val baseUrl: String) : EtatServeur
    data class Echec(val message: String) : EtatServeur
}
