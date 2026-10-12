package com.showcase.android_natif_avec_jetpack_compose_showcase.catalogue

import kotlinx.serialization.Serializable

/**
 * Routes type-safe du catalogue (Navigation Compose + kotlinx.serialization).
 * La leçon 3.3 démontre précisément ce mécanisme ; le catalogue en est l'usage réel.
 */
@Serializable
object RouteAccueil

@Serializable
data class RouteChapitre(val numero: Int)

@Serializable
data class RouteLecon(val chapitre: Int, val numero: Int)

/** Préfixe des deep links : `showcase://lecon/{chapitre}/{numero}`. */
const val BASE_DEEP_LINK_LECON: String = "showcase://lecon"
