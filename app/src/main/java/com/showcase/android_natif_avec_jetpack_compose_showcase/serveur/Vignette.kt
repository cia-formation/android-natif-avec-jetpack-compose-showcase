package com.showcase.android_natif_avec_jetpack_compose_showcase.serveur

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import java.io.ByteArrayOutputStream

/**
 * Génère une vignette PNG à la volée, servie par `/vignette/{graine}`.
 *
 * Intérêt : la leçon 2.4 charge une image **distante** avec Coil. Sans réseau externe, la
 * démo serait morte ; en générant l'image côté serveur embarqué, le chemin complet
 * (requête HTTP, cache disque Coil, décodage) est réellement exercé, hors ligne.
 */
internal fun vignettePng(graine: Int, taille: Int = 320): ByteArray {
    val bitmap = Bitmap.createBitmap(taille, taille, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val palette = listOf(
        0xFF3B82F6.toInt() to 0xFF8B5CF6.toInt(),
        0xFF10B981.toInt() to 0xFF3B82F6.toInt(),
        0xFFF59E0B.toInt() to 0xFFEF4444.toInt(),
        0xFF8B5CF6.toInt() to 0xFFEC4899.toInt(),
        0xFF64748B.toInt() to 0xFF0F172A.toInt(),
    )
    val (debut, fin) = palette[(graine.mod(palette.size))]

    val peinture = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(
            0f, 0f, taille.toFloat(), taille.toFloat(),
            debut, fin, Shader.TileMode.CLAMP,
        )
    }
    canvas.drawRect(0f, 0f, taille.toFloat(), taille.toFloat(), peinture)

    val texte = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = taille / 4f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    canvas.drawText(
        graine.toString(),
        taille / 2f,
        taille / 2f - (texte.ascent() + texte.descent()) / 2f,
        texte,
    )

    return ByteArrayOutputStream().use { flux ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, flux)
        bitmap.recycle()
        flux.toByteArray()
    }
}
