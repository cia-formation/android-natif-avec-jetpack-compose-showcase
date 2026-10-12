package com.showcase.android_natif_avec_jetpack_compose_showcase.serveur

import kotlinx.serialization.Serializable

/** Habitude telle que l'exposent les routes REST du serveur embarqué (modèle du cours). */
@Serializable
data class HabitudeDto(
    val id: String,
    val titre: String,
    val description: String? = null,
    val frequence: String = "quotidienne",
    val couleur: Long = 0xFF3B82F6,
    val creeLe: Long = 0L,
)

/** Corps de création/édition d'une habitude. */
@Serializable
data class HabitudeRequete(
    val titre: String,
    val description: String? = null,
    val frequence: String = "quotidienne",
    val couleur: Long = 0xFF3B82F6,
)

/** Élément de flux paginé, consommé par les démos de listes (leçon 2.6). */
@Serializable
data class ArticleDto(
    val id: Long,
    val titre: String,
    val categorie: String,
)

/** Réponse paginée générique. */
@Serializable
data class PageDto<T>(
    val elements: List<T>,
    val page: Int,
    val total: Int,
)

/** Identifiants acceptés par `/auth/connexion` — volontairement publics, c'est une démo. */
@Serializable
data class ConnexionRequete(val email: String, val motDePasse: String)

@Serializable
data class ConnexionReponse(val jeton: String, val expireDansSecondes: Long)

@Serializable
data class ErreurDto(val code: Int, val message: String)

/** Trame poussée par le WebSocket de démonstration (leçon 6.6). */
@Serializable
data class TrameTempsReel(val sequence: Int, val horodatage: Long, val message: String)
