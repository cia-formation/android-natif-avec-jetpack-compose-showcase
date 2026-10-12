package com.showcase.android_natif_avec_jetpack_compose_showcase

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application du showcase. Deux responsabilités seulement :
 *  - amorcer le graphe Hilt (@HiltAndroidApp), utilisé par les chapitres 4 à 7 ;
 *  - fournir la configuration WorkManager par injection, pour que la leçon 5.5 puisse
 *    déclarer des workers annotés @HiltWorker.
 *
 * La configuration manuelle de WorkManager suppose que l'initialiseur par défaut est
 * retiré du manifeste (voir le `<provider ... tools:node="remove">` de AndroidManifest.xml).
 */
@HiltAndroidApp
class ShowcaseApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var fabriqueWorkers: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(fabriqueWorkers)
            .build()
}
