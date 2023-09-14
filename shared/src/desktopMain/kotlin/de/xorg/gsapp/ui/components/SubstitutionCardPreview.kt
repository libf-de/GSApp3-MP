package de.xorg.gsapp.ui.components;

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionType
import de.xorg.gsapp.data.model.Teacher

import kotlin.OptIn;

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun normalNoNotesPreview() {
        val sampleSubDisp = Substitution(
        type = SubstitutionType.NORMAL,
        klass = "5.3",
        lessonNr = "3",
        origSubject = Subject("En", "Englisch", Color.Red),
        substTeacher = Teacher("KOC", "Fr. Koch"),
        substRoom = "L01",
        substSubject = Subject("De", "Deutsch", Color.Red),
        notes = "",
        isNew = false
        )

        SubstitutionCard(value = sampleSubDisp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun normalWithNotesPreview() {
        val sampleSubDisp = Substitution(
        type = SubstitutionType.NORMAL,
        klass = "5.3",
        lessonNr = "3",
        origSubject = Subject("Ma", "Mathematik", Color.Blue),
        substTeacher = Teacher("BEY", "Fr. Degner-E."),
        substRoom = "L01",
        substSubject = Subject("Re", "Religion", Color.Red),
        notes = "KEIN AUSFALL!",
        isNew = false
        )

        SubstitutionCard(value = sampleSubDisp)
        }


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun cancellationPreview() {
        SubstitutionCard(value = Substitution(
        type = SubstitutionType.CANCELLATION,
        klass = "5.3",
        lessonNr = "4",
        origSubject = Subject("Ma", "Mathematik", Color.Red),
        substTeacher = Teacher("##", "nobody"),
        substRoom = "D12",
        substSubject = Subject("##", "nix", Color.Black),
        notes = "",
        isNew = false
        ))
        }

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun workorderPreview() {
        SubstitutionCard(value = Substitution(
        type = SubstitutionType.WORKORDER,
        klass = "5.3",
        lessonNr = "4",
        origSubject = Subject("Ma", "Mathematik", Color.Red),
        substTeacher = Teacher("WEL", "Welsch, T."),
        substRoom = "L01",
        substSubject = Subject("De", "Deutsch", Color.Blue),
        notes = "AA",
        isNew = false
        ))
        }

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun roomswapPreview() {
        SubstitutionCard(value = Substitution(
        type = SubstitutionType.ROOMSWAP,
        klass = "5.3",
        lessonNr = "4",
        origSubject = Subject("Ma", "Mathematik", Color.Red),
        substTeacher = Teacher("WEL", "Welsch, T."),
        substRoom = "L01",
        substSubject = Subject("Ma", "Mathematik", Color.Red),
        notes = "Raumtausch",
        isNew = false
        ))
        }

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun breastfeedPreview() {
        SubstitutionCard(value = Substitution(
        type = SubstitutionType.BREASTFEED,
        klass = "5.3",
        lessonNr = "4",
        origSubject = Subject("Ma", "Mathematik", Color.Red),
        substTeacher = Teacher("WEL", "Welsch, T."),
        substRoom = "L01",
        substSubject = Subject("Ge", "Mathematik", Color.Red),
        notes = "Stillbesch.",
        isNew = false
        ))
        }
