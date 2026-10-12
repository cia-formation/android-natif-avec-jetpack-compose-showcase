# Showcase — Android natif avec Jetpack Compose

Application Android qui rend **exécutable** chaque leçon du cours
*Android natif avec Jetpack Compose* (CIA Formation, 7 chapitres, 40 leçons, 60 h).

Le cours enseigne du code ; ce dépôt le fait tourner. Vous ouvrez un chapitre, puis une
leçon, et vous manipulez directement les API qu'elle enseigne : réordonner une chaîne de
modifiers et voir le rendu changer, faire défiler mille éléments, provoquer une vraie panne
HTTP pour observer un retry exponentiel.

## Démarrer

```bash
git switch main                 # toutes les démos disponibles
./gradlew :app:installDebug     # installer sur un appareil connecté
```

Le premier build télécharge une toolchain JDK 25 et l'ensemble des dépendances : comptez
une vingtaine de minutes. Les suivants sont de l'ordre de la minute.

Prérequis : un appareil Android (API 28+) avec le mode développeur activé, ou un émulateur.
Aucun compte ni service externe n'est nécessaire — voir *Backends* plus bas.

## Une branche par leçon

```bash
git switch lecon/ch02-l06-listes-performantes
./gradlew :app:installDebug
```

Chaque leçon vit sur sa propre branche, créée depuis `socle` et contenant **uniquement**
sa démo. Le catalogue affiche toujours la carte complète du cours : les leçons absentes de
la branche courante sont marquées « à venir ».

| Branche | Rôle |
|---|---|
| `socle` | Dépendances, coque applicative, catalogue, framework de leçon, serveur embarqué |
| `lecon/chCC-lLL-slug` | Une leçon, une démo |
| `main` | Toutes les leçons mergées |

Les conventions qui rendent ces 40 merges indolores sont dans [CONTRIBUTING.md](CONTRIBUTING.md).

## Architecture

```
app/src/main/java/com/showcase/android_natif_avec_jetpack_compose_showcase/
├── ShowcaseApplication.kt   Hilt + configuration WorkManager injectée
├── MainActivity.kt          Activity unique, navigation entièrement Compose
├── catalogue/               Accueil → Chapitre → Leçon, routes type-safe, deep links
├── socle/                   SpecLecon, LeconScaffold, BlocCode, composants de démo
├── serveur/                 serveur Ktor embarqué (REST, JWT, WebSocket)
└── lecons/chapitreN/        les démos, une par fichier Lecon_C_L.kt
```

Le registre des leçons n'est pas écrit à la main : la tâche Gradle `genererRegistreLecons`
scanne les fichiers `Lecon_*.kt` présents et génère `RegistreLecons.kt`. C'est ce qui
permet à une branche de leçon de n'ajouter qu'un seul fichier.

## Le serveur embarqué

Le cours ne cite qu'une URL fictive (`https://api.habitflow.example`). Pour que les démos
réseau soient réellement vivantes, le showcase **embarque son propre backend** : un serveur
Ktor CIO démarré dans l'application, sur `127.0.0.1` et un port attribué par le système.

Il expose du REST (`/habitudes`, `/articles`), des images PNG générées (`/vignette/{n}`),
un jeton HS256 (`/auth/connexion`), une route protégée (`/securise`), un flux WebSocket
(`/ws/compteur`) et des pannes à la demande (`/panne/{code}`).

Trois conséquences utiles :

- **Tout fonctionne hors ligne**, mode avion compris — c'est de la boucle locale.
- **Les démos sont déterministes** : pas de service tiers qui change ou tombe.
- **Les pannes sont réelles.** La leçon 6.3 (erreurs, retry, offline-first) pilote un taux
  d'échec et une latence côté serveur : le retry exponentiel qu'on observe répond à de
  vraies réponses 503, pas à une simulation côté client.

Le trafic en clair est restreint à la boucle locale par `network_security_config.xml`.

## Backends externes (optionnels)

Les leçons 6.4 (Firebase) et 6.5 (Supabase) contiennent le **code réel et complet**, mais
aucun identifiant n'est commité. Sans configuration, chaque écran affiche sa checklist de
mise en route au lieu de planter, et le reste du showcase fonctionne normalement.

Pour les activer :

- **Firebase** — déposez votre `google-services.json` dans `app/`. Le plugin
  `com.google.gms.google-services` ne s'applique que si ce fichier existe.
- **Supabase** — copiez `SUPABASE_URL` et `SUPABASE_ANON_KEY` depuis
  `local.defaults.properties` vers `local.properties` (ignoré par git) et renseignez-les.

## Chaîne de build

| Composant | Version |
|---|---|
| AGP | 9.4.1 — **Kotlin intégré** (`android.builtInKotlin`), pas de `kotlin-android` |
| Gradle | 9.6, toolchain JDK 25 auto-provisionnée |
| Kotlin (plugins compose et serialization) | 2.4.21 |
| KSP | 2.3.12 |
| Hilt | 2.60.1 |
| Compose BOM | 2026.02.01 (Material 3 1.4.0) |
| compileSdk / targetSdk / minSdk | 37 / 37 / 28 |

Deux contraintes à connaître avant de toucher au build :

- **AGP 9 active le Kotlin intégré**, et `kapt` y est incompatible. Toute génération de code
  passe par `ksp(...)`. KSP doit être en 2.3.1 ou plus, Hilt en 2.59 ou plus.
- **Kotlin ne peut pas rester en arrière.** Coil 3.6.3 et le SDK Supabase 3.8.0 sont
  compilés avec Kotlin 2.4 : leurs métadonnées de classes sont illisibles par un compilateur
  2.2 ou 2.3 (« was compiled with an incompatible version of Kotlin »). D'où Kotlin 2.4.21,
  qui fixe aussi la version des plugins compose et serialization.
- **L'échappatoire existe mais est temporaire.** Si une future version de l'une de ces
  briques cassait, on peut repasser au plugin Kotlin classique avec
  `android.builtInKotlin=false` et `android.newDsl=false` dans `gradle.properties`, plus
  l'application explicite de `org.jetbrains.kotlin.android`. Cette échappatoire **disparaît
  en AGP 10** : à n'utiliser que le temps d'un correctif en amont.

## Vérifier

```bash
./gradlew :app:assembleDebug        # compile
./gradlew :app:testDebugUnitTest    # tests JVM
./gradlew :app:lintDebug            # lint Android
```

Le deep link du catalogue, sur un appareil connecté :

```bash
adb shell am start -W -a android.intent.action.VIEW -d "showcase://lecon/2/6"
```

## Licence et provenance

Support pédagogique de **CIA Formation**, cours 2a du parcours mobile. Les démos sont
écrites en français — identifiants compris — pour rester alignées sur les extraits du
cours (`HabitudeEntity`, `titre`, `observerToutes`).
