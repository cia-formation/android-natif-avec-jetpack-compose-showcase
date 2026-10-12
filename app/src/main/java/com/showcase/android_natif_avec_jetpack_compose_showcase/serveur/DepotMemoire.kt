package com.showcase.android_natif_avec_jetpack_compose_showcase.serveur

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * État du serveur embarqué, en mémoire. Un [Mutex] le protège : les routes Ktor
 * s'exécutent en parallèle sur plusieurs coroutines.
 */
@Singleton
class DepotMemoire @Inject constructor() {

    private val verrou = Mutex()
    private val habitudes = mutableListOf(
        HabitudeDto("h-1", "Boire deux litres d'eau", "Répartis sur la journée", "quotidienne", 0xFF3B82F6, 1_700_000_000_000),
        HabitudeDto("h-2", "Méditer dix minutes", "Au réveil, avant le téléphone", "quotidienne", 0xFF10B981, 1_700_086_400_000),
        HabitudeDto("h-3", "Marcher huit mille pas", null, "quotidienne", 0xFFF59E0B, 1_700_172_800_000),
        HabitudeDto("h-4", "Lire trente minutes", "Papier de préférence", "quotidienne", 0xFF8B5CF6, 1_700_259_200_000),
        HabitudeDto("h-5", "Faire le point de la semaine", "Le dimanche soir", "hebdomadaire", 0xFF64748B, 1_700_345_600_000),
    )

    private val articles = List(1_000) { index ->
        val categories = listOf("Tendances", "Nouveautés", "Films", "Séries", "Documentaires")
        ArticleDto(
            id = index.toLong() + 1,
            titre = "Article ${index + 1} — ${TITRES[index % TITRES.size]}",
            categorie = categories[index % categories.size],
        )
    }

    suspend fun toutesLesHabitudes(): List<HabitudeDto> = verrou.withLock { habitudes.toList() }

    suspend fun habitude(id: String): HabitudeDto? = verrou.withLock { habitudes.firstOrNull { it.id == id } }

    suspend fun creer(requete: HabitudeRequete): HabitudeDto = verrou.withLock {
        val creee = HabitudeDto(
            id = "h-${UUID.randomUUID().toString().take(8)}",
            titre = requete.titre,
            description = requete.description,
            frequence = requete.frequence,
            couleur = requete.couleur,
            creeLe = System.currentTimeMillis(),
        )
        habitudes += creee
        creee
    }

    suspend fun remplacer(id: String, requete: HabitudeRequete): HabitudeDto? = verrou.withLock {
        val index = habitudes.indexOfFirst { it.id == id }
        if (index == -1) return@withLock null
        val miseAJour = habitudes[index].copy(
            titre = requete.titre,
            description = requete.description,
            frequence = requete.frequence,
            couleur = requete.couleur,
        )
        habitudes[index] = miseAJour
        miseAJour
    }

    suspend fun supprimer(id: String): Boolean = verrou.withLock {
        habitudes.removeAll { it.id == id }
    }

    /** Page d'articles, pour les démos de listes et de pull-to-refresh. */
    fun pageArticles(page: Int, taille: Int): PageDto<ArticleDto> {
        val debut = (page * taille).coerceIn(0, articles.size)
        val fin = (debut + taille).coerceIn(0, articles.size)
        return PageDto(elements = articles.subList(debut, fin), page = page, total = articles.size)
    }

    private companion object {
        val TITRES = listOf(
            "composables et recomposition",
            "modifiers dans le bon ordre",
            "Column, Row et Box",
            "thème Material 3",
            "listes paresseuses",
            "formulaires validés",
            "animations d'état",
            "navigation typée",
        )
    }
}
