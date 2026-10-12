package com.showcase.android_natif_avec_jetpack_compose_showcase.serveur

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.server.application.ApplicationCall
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.random.Random

/**
 * Routes du serveur embarqué, regroupées par usage pédagogique :
 *  - `/habitudes`      CRUD REST, support des leçons 6.2, 6.3 et du projet HabitFlow
 *  - `/articles`       pagination, support des listes paresseuses (leçon 2.6)
 *  - `/vignette`       image PNG générée, support de Coil (leçon 2.4)
 *  - `/auth/connexion` émission d'un jeton, support de l'auth JWT (leçons 6.6 et 7.5)
 *  - `/securise`       route protégée par Bearer
 *  - `/ws/compteur`    flux WebSocket, support du temps réel (leçon 6.6)
 *  - `/panne/{code}`   erreur HTTP à la demande
 */
internal fun Application.configurerRoutes(depot: DepotMemoire, chaos: PiloteChaos) {
    routing {
        route("/habitudes") {
            get {
                if (call.perturbe(chaos)) return@get
                call.respond(depot.toutesLesHabitudes())
                chaos.enregistrerSucces()
            }
            get("/{id}") {
                if (call.perturbe(chaos)) return@get
                val id = call.parameters["id"].orEmpty()
                val habitude = depot.habitude(id)
                if (habitude == null) {
                    call.respond(HttpStatusCode.NotFound, ErreurDto(404, "Habitude $id introuvable"))
                } else {
                    call.respond(habitude)
                    chaos.enregistrerSucces()
                }
            }
            post {
                if (call.perturbe(chaos)) return@post
                val requete = call.receive<HabitudeRequete>()
                if (requete.titre.isBlank()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, ErreurDto(422, "Le titre est obligatoire"))
                    return@post
                }
                call.respond(HttpStatusCode.Created, depot.creer(requete))
                chaos.enregistrerSucces()
            }
            put("/{id}") {
                if (call.perturbe(chaos)) return@put
                val id = call.parameters["id"].orEmpty()
                val miseAJour = depot.remplacer(id, call.receive())
                if (miseAJour == null) {
                    call.respond(HttpStatusCode.NotFound, ErreurDto(404, "Habitude $id introuvable"))
                } else {
                    call.respond(miseAJour)
                    chaos.enregistrerSucces()
                }
            }
            delete("/{id}") {
                if (call.perturbe(chaos)) return@delete
                val supprimee = depot.supprimer(call.parameters["id"].orEmpty())
                call.respond(if (supprimee) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
                chaos.enregistrerSucces()
            }
        }

        get("/articles") {
            if (call.perturbe(chaos)) return@get
            val page = call.parameters["page"]?.toIntOrNull() ?: 0
            val taille = (call.parameters["taille"]?.toIntOrNull() ?: 20).coerceIn(1, 200)
            call.respond(depot.pageArticles(page, taille))
            chaos.enregistrerSucces()
        }

        get("/vignette/{graine}") {
            val graine = call.parameters["graine"]?.toIntOrNull() ?: 0
            call.respondBytes(
                bytes = vignettePng(graine),
                contentType = io.ktor.http.ContentType.Image.PNG,
            )
        }

        post("/auth/connexion") {
            val requete = call.receive<ConnexionRequete>()
            if (requete.motDePasse.length < 6) {
                call.respond(HttpStatusCode.Unauthorized, ErreurDto(401, "Mot de passe trop court"))
                return@post
            }
            call.respond(ConnexionReponse(jeton = emettreJeton(requete.email), expireDansSecondes = DUREE_JETON))
        }

        get("/securise") {
            val entete = call.request.headers["Authorization"].orEmpty()
            val sujet = entete.removePrefix("Bearer ").takeIf { it.isNotBlank() }?.let(::verifierJeton)
            if (sujet == null) {
                call.respond(HttpStatusCode.Unauthorized, ErreurDto(401, "Jeton absent ou invalide"))
            } else {
                call.respond(mapOf("sujet" to sujet, "message" to "Accès autorisé"))
            }
        }

        get("/panne/{code}") {
            val code = call.parameters["code"]?.toIntOrNull() ?: 500
            chaos.enregistrerEchec()
            call.respond(HttpStatusCode.fromValue(code), ErreurDto(code, "Panne demandée explicitement"))
        }

        webSocket("/ws/compteur") {
            var sequence = 0
            while (true) {
                sequence += 1
                val trame = TrameTempsReel(
                    sequence = sequence,
                    horodatage = System.currentTimeMillis(),
                    message = "tick $sequence",
                )
                send(Frame.Text(Json.encodeToString(trame)))
                delay(1_000)
            }
        }
    }
}

/**
 * Applique les réglages de chaos avant de servir une route.
 * Renvoie true si la requête a déjà reçu une réponse d'erreur : l'appelant doit s'arrêter là.
 */
private suspend fun ApplicationCall.perturbe(chaos: PiloteChaos): Boolean {
    val reglages = chaos.reglages.value
    if (reglages.latenceMs > 0) delay(reglages.latenceMs)
    if (reglages.horsService) {
        chaos.enregistrerEchec()
        respond(HttpStatusCode.ServiceUnavailable, ErreurDto(503, "Serveur hors service (chaos)"))
        return true
    }
    if (reglages.tauxEchec > 0f && Random.nextFloat() < reglages.tauxEchec) {
        chaos.enregistrerEchec()
        val code = HttpStatusCode.fromValue(reglages.codeEchec)
        respond(code, ErreurDto(reglages.codeEchec, "Panne injectée (chaos)"))
        return true
    }
    return false
}

private const val DUREE_JETON = 3_600L
