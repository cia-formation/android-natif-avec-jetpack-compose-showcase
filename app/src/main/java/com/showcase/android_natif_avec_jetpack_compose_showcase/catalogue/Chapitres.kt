package com.showcase.android_natif_avec_jetpack_compose_showcase.catalogue

/** Metadonnees d'un chapitre du cours, reprises de son \`index.md\`. */
data class MetaChapitre(
    val numero: Int,
    val titre: String,
    val resume: String,
    val dureeMinutes: Int,
    val nombreLecons: Int,
)

/**
 * Les 7 chapitres du cours "Android natif avec Jetpack Compose" (3600 min, ~40 lecons).
 * Table statique : aucune branche de lecon ne la modifie, ce qui garantit des merges sans conflit.
 */
val CHAPITRES: List<MetaChapitre> = listOf(
    MetaChapitre(
        numero = 1,
        titre = "Setup Android Studio et premier projet",
        resume = "Installer Android Studio, configurer le SDK et les emulateurs, creer un premier " +
            "projet Compose et maitriser le build Gradle.",
        dureeMinutes = 300,
        nombreLecons = 4,
    ),
    MetaChapitre(
        numero = 2,
        titre = "Jetpack Compose — les fondamentaux",
        resume = "Composables, modifiers, layouts, texte, images, Material 3, listes performantes, " +
            "formulaires et animations : tout le vocabulaire UI.",
        dureeMinutes = 720,
        nombreLecons = 8,
    ),
    MetaChapitre(
        numero = 3,
        titre = "Navigation et structure d'une app",
        resume = "Structurer une application multi-ecrans avec Navigation Compose, Scaffold, " +
            "TopAppBar et NavigationBar.",
        dureeMinutes = 480,
        nombreLecons = 5,
    ),
    MetaChapitre(
        numero = 4,
        titre = "State management et ViewModels",
        resume = "Gerer l'etat avec remember, StateFlow, ViewModels, UiState scelles, pattern MVVM " +
            "et injection Hilt.",
        dureeMinutes = 480,
        nombreLecons = 5,
    ),
    MetaChapitre(
        numero = 5,
        titre = "Room et persistance locale",
        resume = "Persister les donnees sur l'appareil avec Room, DataStore et WorkManager, pour " +
            "une app fiable et utilisable hors ligne.",
        dureeMinutes = 480,
        nombreLecons = 5,
    ),
    MetaChapitre(
        numero = 6,
        titre = "Reseau et integration backend",
        resume = "Connecter l'app a un backend avec Retrofit et Ktor Client, integrer Firebase et " +
            "Supabase, gerer erreurs, retry et offline-first.",
        dureeMinutes = 600,
        nombreLecons = 6,
    ),
    MetaChapitre(
        numero = 7,
        titre = "Projet integrateur Android",
        resume = "Construire HabitFlow bout-en-bout : Compose, navigation, ViewModels, Hilt, Room " +
            "et appels reseau reunis dans une app livrable.",
        dureeMinutes = 540,
        nombreLecons = 7,
    ),
)

fun chapitreParNumero(numero: Int): MetaChapitre? = CHAPITRES.firstOrNull { it.numero == numero }

/** Une leçon telle que planifiée par le cours, qu'une démo existe ou non sur la branche courante. */
data class PlanLecon(
    val chapitre: Int,
    val numero: Int,
    val titre: String,
    val dureeMinutes: Int,
) {
    val identifiant: String get() = "$chapitre.$numero"
}

/**
 * Le plan complet des 40 leçons du cours. Il permet au catalogue d'afficher la carte
 * entière du cours sur n'importe quelle branche, en marquant « à venir » les leçons
 * dont la démo n'est pas présente dans [LECONS_ENREGISTREES].
 */
val PLAN_LECONS: List<PlanLecon> = listOf(
    PlanLecon(1, 1, "L'écosystème Android moderne", 60),
    PlanLecon(1, 2, "Installation et configuration d'Android Studio", 90),
    PlanLecon(1, 3, "Créer et explorer un projet Compose", 90),
    PlanLecon(1, 4, "Le cycle Gradle et les dépendances Jetpack", 60),
    PlanLecon(2, 1, "Paradigme déclaratif et premier composable", 90),
    PlanLecon(2, 2, "Modifiers : taille, padding, background, click", 90),
    PlanLecon(2, 3, "Layouts fondamentaux : Column, Row, Box", 90),
    PlanLecon(2, 4, "Texte, images et ressources", 90),
    PlanLecon(2, 5, "Material 3 et thème personnalisé", 90),
    PlanLecon(2, 6, "Listes performantes : LazyColumn et LazyRow", 90),
    PlanLecon(2, 7, "Formulaires : TextField, validation et focus", 90),
    PlanLecon(2, 8, "Animations simples et transitions", 90),
    PlanLecon(3, 1, "Anatomie d'une app Android multi-écrans", 60),
    PlanLecon(3, 2, "Navigation Compose : NavController et NavHost", 120),
    PlanLecon(3, 3, "Arguments de navigation et deep links", 90),
    PlanLecon(3, 4, "Scaffold, TopAppBar et BottomNavigation", 120),
    PlanLecon(3, 5, "Patterns d'architecture d'écrans", 90),
    PlanLecon(4, 1, "État en Compose : remember, mutableStateOf, hoisting", 90),
    PlanLecon(4, 2, "ViewModels et cycle de vie Compose", 90),
    PlanLecon(4, 3, "StateFlow, SharedFlow et collectAsStateWithLifecycle", 120),
    PlanLecon(4, 4, "Architecture MVVM en Compose", 90),
    PlanLecon(4, 5, "Injection de dépendances avec Hilt", 90),
    PlanLecon(5, 1, "Stockage local Android : panorama", 60),
    PlanLecon(5, 2, "Room : Entities, DAO et base de données", 120),
    PlanLecon(5, 3, "Room avec Flow et relations", 120),
    PlanLecon(5, 4, "DataStore : préférences modernes", 90),
    PlanLecon(5, 5, "WorkManager : tâches différées et périodiques", 90),
    PlanLecon(6, 1, "Réseau sur Android : OkHttp, Retrofit, Ktor", 90),
    PlanLecon(6, 2, "Retrofit et kotlinx.serialization", 120),
    PlanLecon(6, 3, "Gestion d'erreurs, retry et offline-first", 90),
    PlanLecon(6, 4, "Firebase SDK : Auth, Firestore et Storage", 120),
    PlanLecon(6, 5, "Supabase SDK : Auth, base et Realtime", 90),
    PlanLecon(6, 6, "Ktor Client pour backend custom", 90),
    PlanLecon(7, 1, "Présentation du projet et cadrage fonctionnel", 60),
    PlanLecon(7, 2, "Architecture, modules et squelette du projet", 90),
    PlanLecon(7, 3, "Écrans de listing et de détail", 90),
    PlanLecon(7, 4, "Formulaire de création et édition", 90),
    PlanLecon(7, 5, "Authentification et profil utilisateur", 90),
    PlanLecon(7, 6, "Finitions UI : thème, états vides, erreurs", 60),
    PlanLecon(7, 7, "Bilan, dette technique et prochaines étapes", 60),
)

fun planDuChapitre(chapitre: Int): List<PlanLecon> = PLAN_LECONS.filter { it.chapitre == chapitre }
