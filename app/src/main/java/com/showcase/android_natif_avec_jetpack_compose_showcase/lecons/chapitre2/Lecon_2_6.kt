package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.EtiquetteInfo
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_2_6 = SpecLecon(
    chapitre = 2,
    numero = 6,
    titre = "Listes performantes : LazyColumn et LazyRow",
    resume = "Afficher des milliers d'éléments sans ralentissement grâce à LazyColumn et " +
        "LazyRow, avec clés stables, en-tête collant et pull-to-refresh.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Afficher une liste verticale avec LazyColumn",
        "Afficher une liste horizontale avec LazyRow",
        "Utiliser items avec une clé stable pour la performance",
        "Ajouter un stickyHeader pour une section collante",
        "Savoir quand préférer Lazy à Column + verticalScroll",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "LazyColumn avec clé stable",
            code = """
                @Composable
                fun ListeArticles(articles: List<Article>) {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(items = articles, key = { article -> article.id }) { article ->
                            Text(text = article.titre)
                        }
                    }
                }
            """.trimIndent(),
            commentaire = "La clé aide Compose à savoir quel élément a bougé quand la liste " +
                "change. Sans elle, les éléments sont identifiés par position : les " +
                "animations sont fausses et l'état local est perdu.",
        ),
        ExtraitCode(
            titre = "Le DSL des listes paresseuses",
            code = """
                LazyColumn {
                    item { EnTete() }                                  // un élément unique
                    items(articles, key = { it.id }) { Carte(it) }     // une liste
                    itemsIndexed(articles) { index, a -> Ligne(index, a) }
                    stickyHeader { TitreSection("A") }                 // en-tête collant
                }
            """.trimIndent(),
        ),
        ExtraitCode(
            titre = "Ne composer que le visible, mesuré",
            code = """
                // rememberLazyListState donne accès à ce qui est réellement composé.
                val etat = rememberLazyListState()
                val visibles = etat.layoutInfo.visibleItemsInfo.size
                // 1 000 éléments dans la liste, une vingtaine composés à l'écran.
            """.trimIndent(),
            commentaire = "C'est la mesure qu'affiche la démo : le compteur d'éléments " +
                "composés reste constant alors que la liste en contient mille.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "Column + verticalScroll pour 500 éléments",
            cause = "Habitude prise sur de petites listes.",
            remede = "Passer en LazyColumn dès une vingtaine d'éléments : une Column " +
                "compose tous ses enfants, même hors écran.",
        ),
        Piege(
            probleme = "items sans paramètre key",
            cause = "Le paramètre est optionnel, donc facile à oublier.",
            remede = "Toujours key = { it.id }. C'est gratuit et ça élimine toute une " +
                "catégorie de bugs sur les listes dynamiques.",
        ),
        Piege(
            probleme = "LazyColumn dans une Column sans hauteur",
            cause = "Contraintes verticales infinies dans la même orientation.",
            remede = "Donner une hauteur explicite, ou laisser la Lazy occuper l'écran. " +
                "C'est précisément pourquoi CarteDemo borne la hauteur des démos.",
        ),
        Piege(
            probleme = "État local perdu pendant le défilement",
            cause = "Pas de clé stable, et un remember au lieu d'un rememberSaveable.",
            remede = "Combiner key stable et rememberSaveable pour l'état interne d'un élément.",
        ),
        Piege(
            probleme = "items(ids.size) au lieu de items(liste, key)",
            cause = "Surcharge par index utilisée par réflexe.",
            remede = "La variante par index casse dès que la liste est réordonnée ou filtrée.",
        ),
    ),
    aRetenir = listOf(
        "LazyColumn et LazyRow ne composent que les éléments visibles — défilement fluide garanti",
        "Le DSL expose item, items, itemsIndexed et stickyHeader",
        "La clé stable (key = { it.id }) est indispensable pour des listes dynamiques correctes",
        "Préférez Lazy à Column + verticalScroll dès une vingtaine d'éléments",
        "Une Lazy a besoin de contraintes bornées dans son orientation de défilement",
    ),
    demo = { DemoListesPerformantes() },
)

/** Modèle d'affichage de la démo — l'`id` est ce qui sert de clé stable. */
private data class Article(val id: Long, val titre: String, val categorie: String)

private val CATEGORIES = listOf("Tendances", "Nouveautés", "Films", "Séries", "Documentaires")

private val ARTICLES: List<Article> = List(1_000) { index ->
    Article(
        id = index.toLong() + 1,
        titre = "Article ${index + 1}",
        categorie = CATEGORIES[index % CATEGORIES.size],
    )
}

@Composable
private fun DemoListesPerformantes() {
    var categorieActive by remember { mutableStateOf<String?>(null) }
    val etatListe = rememberLazyListState()

    val articlesAffiches = remember(categorieActive) {
        categorieActive?.let { categorie -> ARTICLES.filter { it.categorie == categorie } }
            ?: ARTICLES
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TitreSection(emoji = "⚡", texte = "1 000 articles, une vingtaine composés")

        // LazyRow : carrousel de filtres, même API que LazyColumn en horizontal.
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = categorieActive == null,
                    onClick = { categorieActive = null },
                    label = { Text("Toutes") },
                )
            }
            items(items = CATEGORIES, key = { it }) { categorie ->
                FilterChip(
                    selected = categorieActive == categorie,
                    onClick = { categorieActive = categorie },
                    label = { Text(categorie) },
                )
            }
        }

        // La mesure qui rend la paresse visible : nombre d'éléments réellement composés.
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EtiquetteInfo("${articlesAffiches.size} dans la liste")
                EtiquetteInfo("${etatListe.layoutInfo.visibleItemsInfo.size} composés")
                EtiquetteInfo("index ${etatListe.firstVisibleItemIndex}")
            }
        }

        HorizontalDivider()

        // La hauteur est bornée par CarteDemo : une Lazy a besoin de contraintes finies
        // dans son orientation de défilement.
        LazyColumn(
            state = etatListe,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(items = articlesAffiches, key = { article -> article.id }) { article ->
                LigneArticle(article)
            }
        }
    }
}

@Composable
private fun LigneArticle(article: Article) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = article.id.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = article.titre, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = article.categorie,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "2.6 clair", showBackground = true)
@Composable
private fun ApercuLecon26Clair() {
    ShowcaseTheme(sombre = false) { DemoListesPerformantes() }
}

@Preview(name = "2.6 sombre", showBackground = true)
@Composable
private fun ApercuLecon26Sombre() {
    ShowcaseTheme(sombre = true) { DemoListesPerformantes() }
}
