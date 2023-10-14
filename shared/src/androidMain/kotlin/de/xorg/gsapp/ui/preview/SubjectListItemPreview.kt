package de.xorg.gsapp.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.ui.components.settings.SubjectListItem

@Composable
@Preview
fun SubjectListItemPreview() {
    SubjectListItem(
        subject = Subject(
            "de",
            "Deutsch",
            Color.Blue
        )
        , onClick = { /*TODO*/ }, onDelete = {

        })
}