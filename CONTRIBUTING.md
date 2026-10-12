# Conventions du showcase

Ce dépôt n'est pas une application ordinaire : c'est **une branche par leçon** du cours
*Android natif avec Jetpack Compose*, plus une branche d'intégration. Les règles ci-dessous
existent pour une raison précise : garantir que les 40 branches de leçons se mergent
**sans aucun conflit**.

## Modèle de branches

| Branche | Contenu |
|---|---|
| `socle` | Toutes les dépendances, la coque applicative, le catalogue, le framework de leçon, le serveur embarqué. **Aucune démo.** |
| `lecon/chCC-lLL-slug` | Exactement **une** leçon. Créée depuis `socle`, jamais depuis une autre leçon. |
| `main` | Branche d'intégration : `socle` + toutes les leçons mergées en ordre. |

Exemples : `lecon/ch02-l06-listes-performantes`, `lecon/ch07-l03-listing-detail`.

Pour exécuter une leçon seule :

```bash
git switch lecon/ch02-l06-listes-performantes
./gradlew :app:installDebug
```

Le catalogue affiche alors la carte complète des 7 chapitres et des 40 leçons, mais seule
la démo présente sur la branche est ouvrable — les autres sont marquées « à venir ».

## Les 5 règles

1. **Une branche de leçon ne touche que `lecons/chapitreN/`.**
   Plus, au besoin, un fichier de ressources qui lui est propre :
   `res/values/strings_lecon_C_L.xml`. Rien d'autre.

2. **Ne jamais modifier les fichiers du socle.**
   `build.gradle.kts`, `gradle/libs.versions.toml`, `AndroidManifest.xml`, `ui/theme/`,
   `catalogue/`, `socle/`, `serveur/` sont posés une fois pour toutes. Toutes les
   dépendances du cours sont déjà déclarées : une leçon n'a jamais besoin d'en ajouter une.
   Si une leçon semble exiger une modification du socle, c'est le socle qu'il faut corriger
   — sur la branche `socle`, puis rebaser les leçons.

3. **Chaque leçon déclare sa propre base Room.**
   Ses `@Entity`, ses `@Dao` et sa `@Database` vivent dans son package, avec
   `exportSchema = false`. Pas de base partagée à faire évoluer d'une leçon à l'autre, donc
   pas de migration ni de conflit de schéma.

4. **Chaque leçon déclare son propre module Hilt, et ne *bind* que ses propres types.**
   Deux leçons qui fourniraient le même type produiraient un doublon de binding au moment
   du merge — une erreur de compilation que seule la branche d'intégration verrait.
   En pratique : préfixez vos types par la leçon (`DepotLecon52`, pas `Depot`).

5. **Les chaînes d'une leçon vont dans son propre fichier de ressources.**
   `strings_lecon_C_L.xml` : la fusion de ressources Android accepte plusieurs fichiers,
   donc aucun conflit. Ne touchez pas à `res/values/strings.xml`.

## Le registre des leçons est généré

Un registre écrit à la main serait modifié par les 40 branches, donc 40 conflits. La tâche
Gradle `genererRegistreLecons` scanne les fichiers `lecons/chapitre*/Lecon_*.kt` présents,
en extrait le package et le `val` top-level, et génère `RegistreLecons.kt`.

Conséquence : **une branche de leçon n'ajoute qu'un fichier Kotlin**, et le catalogue le
découvre tout seul. Le fichier généré n'est jamais commité.

Pour l'inspecter :

```bash
./gradlew :app:genererRegistreLecons
cat app/build/generated/*/*/com/showcase/*/catalogue/RegistreLecons.kt
```

## Écrire une leçon

Un fichier `lecons/chapitreC/Lecon_C_L.kt` qui expose **un seul `val` top-level** nommé
`lecon_C_L` :

```kotlin
package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

val lecon_2_6 = SpecLecon(
    chapitre = 2,
    numero = 6,
    titre = "Listes performantes : LazyColumn et LazyRow",
    resume = "…",                  // @short_description de la leçon source
    dureeMinutes = 90,
    objectifs = listOf(…),         // section 🎯 Objectifs
    extraits = listOf(…),          // extraits de code commentés
    pieges = listOf(…),            // tableau ⚠️ Pièges fréquents
    aRetenir = listOf(…),          // section 📌 En résumé
    demo = { DemoListesPerformantes() },
)
```

Le nom du `val` est ce que la tâche de génération cherche : respectez exactement
`lecon_<chapitre>_<numero>`.

### Attendus d'une démo

- **Interactive.** Une démo qui ne fait que s'afficher n'apprend rien : donnez des boutons,
  des sliders, des sélecteurs qui montrent l'effet de l'API enseignée.
- **Bornée en hauteur.** `CarteDemo` borne la hauteur, ce qui permet à une démo d'embarquer
  sa propre `LazyColumn` alors que l'écran de leçon est déjà scrollable. Pour une mini-app
  qui a besoin de tout l'écran, passez `pleinEcran = true`.
- **Prévisualisable.** Ajoutez une `@Preview` claire et une sombre : c'est la vérification
  de rendu qui ne demande pas d'appareil.
- **Fidèle au cours.** Les identifiants sont en français, comme les extraits du cours
  (`HabitudeEntity`, `titre`, `observerToutes`). Quand le showcase utilise une API plus
  récente que celle citée par la leçon, signalez l'écart dans `pieges`.

## Vérifier avant de commiter

```bash
./gradlew :app:assembleDebug            # compile
./gradlew :app:testDebugUnitTest        # tests JVM (ViewModels, DAO, serveur)
git diff --stat socle..HEAD             # ne doit lister que des fichiers sous lecons/
```

La dernière commande est la plus importante : **si elle montre autre chose que
`lecons/`, le merge dans `main` entrera en conflit.**
