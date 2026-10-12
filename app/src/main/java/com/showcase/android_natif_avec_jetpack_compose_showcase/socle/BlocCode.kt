package com.showcase.android_natif_avec_jetpack_compose_showcase.socle

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Mots-cles Kotlin coloriser dans les extraits du showcase. */
private val MOTS_CLES = setOf(
    "package", "import", "fun", "val", "var", "class", "object", "interface", "data",
    "sealed", "enum", "abstract", "override", "private", "internal", "public", "suspend",
    "return", "if", "else", "when", "for", "while", "is", "in", "as", "by", "it",
    "true", "false", "null", "this", "super", "companion", "init", "const", "lateinit",
    "typealias", "where", "out", "vararg", "operator", "inline", "crossinline", "noinline",
)

private val REGEX_JETON = Regex("""(//[^\n]*)|("(?:\\.|[^"\\])*")|(@\w+)|(\b[A-Za-z_]\w*\b)""")

/**
 * Coloration syntaxique volontairement minimale : commentaires, chaines, annotations,
 * mots-cles. Pas de parseur Kotlin embarque, le but est la lisibilite d'un extrait court.
 */
@Composable
fun codeColorise(code: String): AnnotatedString {
    val couleurCommentaire = MaterialTheme.colorScheme.onSurfaceVariant
    val couleurChaine = MaterialTheme.colorScheme.tertiary
    val couleurMotCle = MaterialTheme.colorScheme.primary
    val couleurAnnotation = MaterialTheme.colorScheme.secondary
    val couleurType = MaterialTheme.colorScheme.onSurface

    return buildAnnotatedString {
        var curseur = 0
        for (jeton in REGEX_JETON.findAll(code)) {
            if (jeton.range.first > curseur) append(code.substring(curseur, jeton.range.first))
            val texte = jeton.value
            val couleur: Color? = when {
                texte.startsWith("//") -> couleurCommentaire
                texte.startsWith("\"") -> couleurChaine
                texte.startsWith("@") -> couleurAnnotation
                texte in MOTS_CLES -> couleurMotCle
                texte.first().isUpperCase() -> couleurType
                else -> null
            }
            if (couleur == null) {
                append(texte)
            } else {
                val gras = texte in MOTS_CLES || texte.startsWith("@")
                withStyle(SpanStyle(color = couleur, fontWeight = if (gras) FontWeight.SemiBold else null)) {
                    append(texte)
                }
            }
            curseur = jeton.range.last + 1
        }
        if (curseur < code.length) append(code.substring(curseur))
    }
}

/** Affiche un extrait de code Kotlin, scrollable horizontalement pour ne jamais casser les lignes. */
@Composable
fun BlocCode(extrait: ExtraitCode, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = extrait.titre,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = codeColorise(extrait.code.trim()),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                softWrap = false,
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(14.dp),
            )
        }
        extrait.commentaire?.let { commentaire ->
            Text(
                text = commentaire,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
