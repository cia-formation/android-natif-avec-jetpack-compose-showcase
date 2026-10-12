package com.showcase.android_natif_avec_jetpack_compose_showcase.lecons.chapitre2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.ExtraitCode
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.Piege
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.SpecLecon
import com.showcase.android_natif_avec_jetpack_compose_showcase.socle.TitreSection
import com.showcase.android_natif_avec_jetpack_compose_showcase.ui.theme.ShowcaseTheme

val lecon_2_7 = SpecLecon(
    chapitre = 2,
    numero = 7,
    titre = "Formulaires : TextField, validation et focus",
    resume = "Construire un formulaire complet avec TextField et OutlinedTextField, gérer " +
        "le clavier, le focus entre champs et valider les saisies en temps réel.",
    dureeMinutes = 90,
    objectifs = listOf(
        "Construire un formulaire avec TextField et OutlinedTextField",
        "Configurer le type de clavier et la touche IME avec keyboardOptions",
        "Enchaîner le focus d'un champ au suivant avec FocusRequester",
        "Valider les saisies en temps réel avec isError et supportingText",
        "Désactiver le bouton d'envoi tant que la validation globale échoue",
    ),
    extraits = listOf(
        ExtraitCode(
            titre = "Un TextField est un composable contrôlé",
            code = """
                var email by rememberSaveable { mutableStateOf("") }

                OutlinedTextField(
                    value = email,                      // l'état vient de l'extérieur
                    onValueChange = { email = it },     // le composable ne le modifie pas lui-même
                    label = { Text("Adresse e-mail") },
                )
            """.trimIndent(),
            commentaire = "Le champ n'a pas d'état interne : c'est le même state hoisting " +
                "que la leçon 4.1 généralise. rememberSaveable survit à la rotation.",
        ),
        ExtraitCode(
            titre = "Clavier adapté et touche IME",
            code = """
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,   // clavier avec @
                        imeAction = ImeAction.Next,          // touche « suivant »
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusMotDePasse.requestFocus() },
                    ),
                )
            """.trimIndent(),
        ),
        ExtraitCode(
            titre = "Validation qui ne crie pas trop tôt",
            code = """
                // Le champ vide n'est pas « invalide » : il n'a simplement pas été rempli.
                val emailInvalide = email.isNotEmpty() && !email.contains("@")

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    isError = emailInvalide,
                    supportingText = {
                        if (emailInvalide) Text("Adresse e-mail incomplète")
                    },
                )
            """.trimIndent(),
            commentaire = "La garde isNotEmpty() évite le formulaire rouge dès l'ouverture, " +
                "qui est le défaut le plus courant des formulaires Compose débutants.",
        ),
    ),
    pieges = listOf(
        Piege(
            probleme = "Clavier alphabétique pour saisir un nombre",
            cause = "keyboardType non précisé.",
            remede = "KeyboardOptions(keyboardType = KeyboardType.Number). Chaque champ " +
                "mérite son clavier : Email, Number, Password, Phone.",
        ),
        Piege(
            probleme = "Erreur affichée dès l'ouverture du formulaire",
            cause = "La validation ne distingue pas « vide » de « invalide ».",
            remede = "Exclure le cas vide : email.isNotEmpty() && !email.contains(\"@\").",
        ),
        Piege(
            probleme = "La touche « suivant » ne déplace pas le focus",
            cause = "imeAction déclaré, mais aucun FocusRequester câblé derrière.",
            remede = "Déclarer un FocusRequester par champ et appeler requestFocus() dans " +
                "keyboardActions.onNext.",
        ),
        Piege(
            probleme = "Saisie perdue à la rotation de l'écran",
            cause = "remember seul ne survit pas au changement de configuration.",
            remede = "rememberSaveable pour tout ce que l'utilisateur a tapé.",
        ),
        Piege(
            probleme = "Bouton d'envoi actif alors que le formulaire est invalide",
            cause = "enabled non conditionné.",
            remede = "enabled = formulaireValide : l'utilisateur voit tout de suite s'il " +
                "peut soumettre, sans avoir à essayer.",
        ),
    ),
    aRetenir = listOf(
        "TextField et OutlinedTextField sont contrôlés : état externe, callback onValueChange",
        "keyboardOptions configure le type de clavier et la touche IME",
        "FocusRequester et keyboardActions enchaînent le focus d'un champ au suivant",
        "isError et supportingText affichent une erreur en temps réel",
        "Un formulaire soigné désactive son bouton d'envoi tant que la validation globale échoue",
    ),
    demo = { DemoFormulaire() },
)

@Composable
private fun DemoFormulaire() {
    var titre by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var objectif by rememberSaveable { mutableStateOf("") }
    var motDePasse by rememberSaveable { mutableStateOf("") }
    var envoye by remember { mutableStateOf(false) }

    val focusEmail = remember { FocusRequester() }
    val focusObjectif = remember { FocusRequester() }
    val focusMotDePasse = remember { FocusRequester() }
    val clavier = LocalSoftwareKeyboardController.current

    // La garde isNotEmpty() distingue « pas encore rempli » de « mal rempli ».
    val titreInvalide = titre.isNotEmpty() && titre.length < 3
    val emailInvalide = email.isNotEmpty() && !(email.contains("@") && email.substringAfter("@").contains("."))
    val objectifInvalide = objectif.isNotEmpty() && objectif.toIntOrNull()?.let { it !in 1..100 } ?: true
    val motDePasseInvalide = motDePasse.isNotEmpty() && motDePasse.length < 6

    val formulaireValide = titre.length >= 3 &&
        !emailInvalide && email.isNotEmpty() &&
        !objectifInvalide && objectif.isNotEmpty() &&
        motDePasse.length >= 6

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TitreSection(emoji = "📝", texte = "Nouvelle habitude")

        OutlinedTextField(
            value = titre,
            onValueChange = { titre = it; envoye = false },
            label = { Text("Titre de l'habitude") },
            isError = titreInvalide,
            supportingText = {
                Text(
                    text = if (titreInvalide) {
                        "Trois caractères minimum"
                    } else {
                        "Par exemple : Boire deux litres d'eau"
                    },
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusEmail.requestFocus() }),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; envoye = false },
            label = { Text("E-mail de rappel") },
            isError = emailInvalide,
            supportingText = {
                Text(if (emailInvalide) "Adresse e-mail incomplète" else "Clavier de type Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusObjectif.requestFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusEmail),
            singleLine = true,
        )

        OutlinedTextField(
            value = objectif,
            onValueChange = { nouvelle -> objectif = nouvelle.filter { it.isDigit() }; envoye = false },
            label = { Text("Objectif quotidien") },
            isError = objectifInvalide && objectif.isNotEmpty(),
            supportingText = {
                Text(
                    text = if (objectifInvalide && objectif.isNotEmpty()) {
                        "Un nombre entre 1 et 100"
                    } else {
                        "Clavier numérique, saisie filtrée aux chiffres"
                    },
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusMotDePasse.requestFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusObjectif),
            singleLine = true,
        )

        OutlinedTextField(
            value = motDePasse,
            onValueChange = { motDePasse = it; envoye = false },
            label = { Text("Mot de passe") },
            isError = motDePasseInvalide,
            supportingText = {
                Text(if (motDePasseInvalide) "Six caractères minimum" else "Saisie masquée")
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { clavier?.hide() }),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusMotDePasse),
            singleLine = true,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = { envoye = true; clavier?.hide() },
                enabled = formulaireValide,
            ) {
                Text("Enregistrer")
            }
            Text(
                text = if (formulaireValide) {
                    "Validation globale satisfaite."
                } else {
                    "Bouton désactivé tant que tous les champs ne sont pas valides."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        if (envoye) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "« $titre » enregistrée, objectif $objectif, rappel vers $email.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}

@Preview(name = "2.7 clair", showBackground = true)
@Composable
private fun ApercuLecon27Clair() {
    ShowcaseTheme(sombre = false) { DemoFormulaire() }
}

@Preview(name = "2.7 sombre", showBackground = true)
@Composable
private fun ApercuLecon27Sombre() {
    ShowcaseTheme(sombre = true) { DemoFormulaire() }
}
