package com.showcase.android_natif_avec_jetpack_compose_showcase.catalogue

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute

/**
 * Graphe de navigation du showcase : Accueil → Chapitre → Leçon.
 * Posé sur la branche socle, il n'est jamais modifié par une branche de leçon.
 */
@Composable
fun NavigationCatalogue(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = RouteAccueil,
        modifier = modifier,
        enterTransition = { slideInHorizontally(tween(260)) { it / 6 } },
        exitTransition = { slideOutHorizontally(tween(260)) { -it / 6 } },
        popEnterTransition = { slideInHorizontally(tween(260)) { -it / 6 } },
        popExitTransition = { slideOutHorizontally(tween(260)) { it / 6 } },
    ) {
        composable<RouteAccueil> {
            EcranAccueil(
                surChapitre = { numero -> navController.navigate(RouteChapitre(numero)) },
            )
        }
        composable<RouteChapitre> { entree ->
            val route = entree.toRoute<RouteChapitre>()
            EcranChapitre(
                numeroChapitre = route.numero,
                surLecon = { chapitre, numero -> navController.navigate(RouteLecon(chapitre, numero)) },
                surRetour = { navController.popBackStack() },
            )
        }
        composable<RouteLecon>(
            deepLinks = listOf(navDeepLink<RouteLecon>(basePath = BASE_DEEP_LINK_LECON)),
        ) { entree ->
            val route = entree.toRoute<RouteLecon>()
            EcranLecon(
                chapitre = route.chapitre,
                numero = route.numero,
                surRetour = {
                    if (!navController.popBackStack()) {
                        // Arrivée par deep link : pas de back stack à dépiler, on rejoint l'accueil.
                        navController.navigate(RouteAccueil)
                    }
                },
            )
        }
    }
}
