package de.xorg.gsapp.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.xorg.gsapp.ui.components.ClassListItem
import de.xorg.gsapp.ui.components.SkeletonClassListItem

@Composable
@Preview
fun ClassListItemPreview() {
    ClassListItem(
        label = "10.3",
        highlight = false,
    ) { }
}

@Composable
@Preview
fun SkeletonClassListItemPreview() {
    SkeletonClassListItem()
}