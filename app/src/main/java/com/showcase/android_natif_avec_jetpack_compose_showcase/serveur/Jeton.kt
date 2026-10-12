package com.showcase.android_natif_avec_jetpack_compose_showcase.serveur

import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * JWT HS256 minimal, signé et vérifié localement.
 *
 * Pourquoi pas `ktor-server-auth-jwt` : cette dépendance tire auth0/java-jwt côté serveur,
 * alors que le serveur n'est ici qu'un support de démonstration. Trente lignes de `Mac`
 * produisent un jeton **réellement conforme** (header.payload.signature en base64url), ce qui
 * suffit : le sujet enseigné par la leçon 6.6 est le plugin `Auth` côté *client*, pas
 * l'implémentation du serveur.
 *
 * Le secret est public et en dur : c'est un bac à sable sur la boucle locale, aucune donnée
 * réelle n'y transite. Ne jamais reproduire ce schéma pour un vrai backend.
 */
private const val SECRET_DEMO = "secret-de-demonstration-showcase-compose"

private fun base64Url(donnees: ByteArray): String =
    Base64.encodeToString(donnees, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

private fun signer(contenu: String): String {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(SECRET_DEMO.toByteArray(), "HmacSHA256"))
    return base64Url(mac.doFinal(contenu.toByteArray()))
}

/** Émet un jeton HS256 valable [dureeSecondes] pour le sujet donné. */
internal fun emettreJeton(sujet: String, dureeSecondes: Long = 3_600L): String {
    val maintenant = System.currentTimeMillis() / 1_000
    val entete = base64Url("""{"alg":"HS256","typ":"JWT"}""".toByteArray())
    val charge = base64Url(
        """{"sub":"$sujet","iat":$maintenant,"exp":${maintenant + dureeSecondes}}""".toByteArray(),
    )
    val contenu = "$entete.$charge"
    return "$contenu.${signer(contenu)}"
}

/** Vérifie signature et expiration ; renvoie le sujet, ou null si le jeton est invalide. */
internal fun verifierJeton(jeton: String): String? {
    val morceaux = jeton.split('.')
    if (morceaux.size != 3) return null
    val (entete, charge, signature) = morceaux
    if (signer("$entete.$charge") != signature) return null

    val chargeJson = runCatching {
        Json.parseToJsonElement(String(Base64.decode(charge, Base64.URL_SAFE))) as JsonObject
    }.getOrNull() ?: return null

    val expiration = chargeJson["exp"]?.jsonPrimitive?.content?.toLongOrNull() ?: return null
    if (expiration < System.currentTimeMillis() / 1_000) return null
    return chargeJson["sub"]?.jsonPrimitive?.content
}
