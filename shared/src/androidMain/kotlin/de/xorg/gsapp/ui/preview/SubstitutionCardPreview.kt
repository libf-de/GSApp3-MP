package de.xorg.gsapp.ui.preview

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionType
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.ui.components.SubstitutionCard

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NormalNoNotesPreview() {
    val sampleSubDisp = Substitution(
        type = SubstitutionType.NORMAL,
        klass = "5.3",
        lessonNr = "3",
        origSubject = Subject("En", "Englisch", Color.Red),
        substTeacher = Teacher("KOC", "Degner-Engelhard, Fr."),
        substRoom = "L01",
        substSubject = Subject("De", "Deutsch", Color.Red),
        notes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
        isNew = false
    )

    SubstitutionCard(value = sampleSubDisp)
}