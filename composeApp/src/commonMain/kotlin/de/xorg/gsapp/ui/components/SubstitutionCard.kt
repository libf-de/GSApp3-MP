/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.xorg.gsapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionType
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.materialtools.ColorRoles
import de.xorg.gsapp.ui.materialtools.MaterialColors
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun SubstitutionCard(
    value: Substitution,
    modifier: Modifier = Modifier
) {
    val harmonizedColor = MaterialColors.harmonize(
        colorToHarmonize = value.origSubject.color.toArgb(),
        colorToHarmonizeWith = MaterialTheme.colorScheme.primary.toArgb())
    val colorRoles = MaterialColors.getColorRoles(color = harmonizedColor,
        isLightTheme = isSystemInDarkTheme())


    var isExpanded by remember { mutableStateOf(false) }

    Card(modifier = modifier
        .animateContentSize()
        .padding(vertical = 4.dp)
        .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = Color(colorRoles.accentContainer))) {

        AnimatedContent(
            targetState = isExpanded
        ) { expanded ->
            when(expanded) {
                true -> LargeCardContent(
                    value = value,
                    colorRoles = colorRoles
                )
                false -> SmallCardContent(
                    value = value,
                    colorRoles = colorRoles
                )
            }
        }

        /**** begin Card RowLayout **/

    }
}

@Composable
private fun LargeCardContent(
    value: Substitution,
    colorRoles: ColorRoles
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val style = MaterialTheme.typography.bodyLarge

        IconAndText(
            text = "${value.klass}, ${value.lessonNr}. Stunde",
            icon = painterResource(MR.images.class_lesson),
            contentDescription = "Klasse / Stunde",
            tint = Color(colorRoles.onAccentContainer),
            style = style,
            spacedBy = 8.dp
        )

        IconAndText(
            text = value.origSubject.longName,
            icon = painterResource(MR.images.original_subject),
            contentDescription = "zu vertretendes Fach",
            tint = Color(colorRoles.onAccentContainer),
            style = style,
            spacedBy = 8.dp
        )

        IconAndText(
            text = value.substSubject.longName,
            icon = painterResource(MR.images.replacement_subject),
            contentDescription = "Ersatzfach",
            tint = Color(colorRoles.onAccentContainer),
            style = style,
            spacedBy = 8.dp
        )

        LocationItem(
            value = value,
            style = style,
            spacedBy = 8.dp,
            tint = Color(colorRoles.onAccentContainer),
        )

        TeacherItem(
            value = value.substTeacher,
            style = style,
            spacedBy = 8.dp,
            tint = Color(colorRoles.onAccentContainer),
        )

        IconAndText(
            text = value.notes.ifBlank { "Keine Bemerkungen" },
            icon = Icons.Rounded.Info,
            contentDescription = "Bemerkungen",
            tint = Color(colorRoles.onAccentContainer),
            softWrap = true,
            spacedBy = 8.dp,
            style = style
        )
    }
}

@Composable
private fun SmallCardContent(
    value: Substitution,
    colorRoles: ColorRoles
) {
    var totalWidth by remember { mutableStateOf(64.dp) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .onGloballyPositioned {totalWidth = it.size.width.dp }
    ) {
        /**** begin LessonNumber | w=55.dp **/
        LessonNr(
            value = value.lessonNrAsString(),
            colorRoles = colorRoles,
            modifier = Modifier
                .padding(start = 15.dp,
                    top = 15.dp,
                    bottom = 15.dp)
                .fillMaxHeight()
        )
        /** end LessonNumber ****/

        /** begin contentbox **/
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxHeight()
                .maybeReserveNotesSpace(
                    reserve = value.shouldDisplayNotes(),
                    totalWidth = totalWidth
                )
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {

                /**** begin ContentBox -> Title **/
                Text(
                    text = value.getCardTitle(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 4.dp),
                    color = Color(colorRoles.onAccentContainer)
                )
                /** end ContentBox -> Title ****/

                /**** begin ContentBox --> SubBox **/
                Row {
                    /* ContentBox -> SubBox -> Room */
                    LocationItem(
                        value = value,
                        tint = Color(colorRoles.onAccentContainer),
                        modifier = Modifier.padding(end = 8.dp),
                        isSmall = true
                    )

                    /* ContentBox -> SubBox -> Teacher */
                    TeacherItem(
                        value = value.substTeacher,
                        tint = Color(colorRoles.onAccentContainer),
                        modifier = Modifier.padding(end = 8.dp),
                        isSmall = true
                    )
                }
                /** end ContentBox -> SubBox ****/

            } /* end ContentBox column */
        }
        /** end ContentBox ****/

        if (value.shouldDisplayNotes()) {
            NotesBox(
                value = value,
                colorRoles = colorRoles,
                modifier = Modifier
                    .defaultMinSize(minWidth = (totalWidth - 55.dp) / 3)
                    .wrapContentSize()
                    .fillMaxHeight(),
            )
        }
    } /** end Card RowLayout ****/
}

@Composable
private fun LargeContentBox(
    value: Substitution,
    colorRoles: ColorRoles,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row {
            Icon(
                painter = painterResource(MR.images.groups),
                contentDescription = "Klasse",
                modifier = Modifier.alignByBaseline().size(24.dp),
                tint = Color(colorRoles.onAccentContainer)
            )

            Text(
                text = value.klass,
                modifier = Modifier.alignByBaseline().wrapContentWidth(),
                softWrap = false,
                color = Color(colorRoles.onAccentContainer)
            )
        }
        Row {
            Icon(
                painter = painterResource(MR.images.original_subject),
                contentDescription = "zu vertretendes Fach",
                modifier = Modifier.alignByBaseline().size(24.dp),
                tint = Color(colorRoles.onAccentContainer)
            )
            Text(
                text = value.origSubject.longName,
                modifier = Modifier.alignByBaseline().wrapContentWidth(),
                softWrap = false,
                color = Color(colorRoles.onAccentContainer)
            )
        }
        Row {
            Icon(
                painter = painterResource(MR.images.replacement_subject),
                contentDescription = "Ersatzfach",
                modifier = Modifier.alignByBaseline().size(24.dp),
                tint = Color(colorRoles.onAccentContainer)
            )
            Text(
                text = value.substSubject.longName,
                modifier = Modifier.alignByBaseline().wrapContentWidth(),
                softWrap = false,
                color = Color(colorRoles.onAccentContainer)
            )
        }
        Row {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = "Raum",
                modifier = Modifier.alignByBaseline().size(24.dp),
                tint = Color(colorRoles.onAccentContainer),
            )
            Text(
                text = value.substRoom,
                modifier = Modifier.alignByBaseline().wrapContentWidth(),
                softWrap = false,
                color = Color(colorRoles.onAccentContainer)
            )
        }
        if(value.hasSubstTeacher) {
            Row {
                Icon(imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(MR.strings.subplan_dsc_teacher),
                    modifier = Modifier.alignByBaseline().size(24.dp),
                    tint = Color(colorRoles.onAccentContainer))

                Text(text = value.substTeacher.longName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .wrapContentWidth()
                        .alignByBaseline(),
                    softWrap = false,
                    color = Color(colorRoles.onAccentContainer))
            }
        }
        if(value.shouldDisplayNotes()) {
            Row {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Bemerkungen",
                    modifier = Modifier.alignByBaseline().size(24.dp),
                    tint = Color(colorRoles.onAccentContainer)
                )
                Text(
                    text = value.notes,
                    modifier = Modifier.alignByBaseline().wrapContentWidth(),
                    softWrap = false,
                    color = Color(colorRoles.onAccentContainer)
                )
            }
        }

    }
}

@Composable
private fun SmallContentBox(
    value: Substitution,
    totalWidth: Dp,
    colorRoles: ColorRoles,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun LessonNr(
    value: String,
    colorRoles: ColorRoles,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.CenterStart,
        modifier = modifier ) {

        /* LessonNumber -> Circle Background */
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(colorRoles.accent))
        ) {
            Text(text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color(colorRoles.onAccent),
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(bottom = 1.5f.dp))
        }
    }
}

@Composable
private fun IconAndText(
    text: String,
    icon: ImageVector,
    tint: Color,
    spacedBy: Dp = 4.dp,
    contentDescription: String? = null,
    fontWeight: FontWeight? = MaterialTheme.typography.bodyMedium.fontWeight,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    softWrap: Boolean = false,
    modifier: Modifier = Modifier,
    isSmall: Boolean = false
) {
    IconAndText(
        text = text,
        icon = rememberVectorPainter(icon),
        tint = tint,
        contentDescription = contentDescription,
        fontWeight = fontWeight,
        style = style,
        softWrap = softWrap,
        modifier = modifier,
        spacedBy = spacedBy,
        isSmall = isSmall
    )
}


@Composable
private fun IconAndText(
    text: String,
    icon: Painter,
    tint: Color,
    spacedBy: Dp = 4.dp,
    contentDescription: String? = null,
    fontWeight: FontWeight? = MaterialTheme.typography.bodyMedium.fontWeight,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    softWrap: Boolean = false,
    modifier: Modifier = Modifier,
    isSmall: Boolean = false
    ){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacedBy),
    ) {
        Icon(painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(24.dp)
                .alignByBaseline(),
            tint = tint
        )
        Text(
            text = text,
            style = style,
            fontWeight = fontWeight,
            modifier = Modifier
                .padding(top = if(isSmall) 2.dp else 0.dp)
                .alignByBaseline(),
            softWrap = softWrap,
            color = tint
        )
    }
}

@Composable
private fun LocationItem(
    value: Substitution,
    tint: Color,
    isSmall: Boolean = false,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    spacedBy: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    IconAndText(
        text = value.substRoom,
        icon = Icons.Outlined.LocationOn,
        tint = tint,
        style = style,
        contentDescription = stringResource(MR.strings.subplan_dsc_location),
        fontWeight = if (value.type == SubstitutionType.ROOMSWAP)
            FontWeight.ExtraBold
        else
            style.fontWeight,
        modifier = modifier,
        spacedBy = spacedBy,
        isSmall = isSmall
    )
}

@Composable
private fun TeacherItem(
    value: Teacher,
    tint: Color,
    isSmall: Boolean = false,
    spacedBy: Dp = 4.dp,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    modifier: Modifier = Modifier
) {
    if(value.shortName != "##" && value.shortName.isNotEmpty()) {
        IconAndText(
            text = value.longName,
            icon = Icons.Outlined.Person,
            tint = tint,
            style = style,
            spacedBy = spacedBy,
            contentDescription = stringResource(MR.strings.subplan_dsc_teacher),
            modifier = modifier,
            isSmall = isSmall
        )
    }
}

@Composable
fun NotesBox(
    value: Substitution,
    colorRoles: ColorRoles,
    modifier: Modifier = Modifier,
) {
    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier) {

        /* NotesBox -> Divider */
        Divider(color = Color(colorRoles.accent),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxHeight()
                .width(1.dp) )


        /* NotesBox -> Notes */
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Text(text = value.notes,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = Color(colorRoles.accent),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 8.dp,
                        top = 13.dp,
                        end = 15.dp,
                        bottom = 12.dp)
                    .wrapContentSize()
            )
        }
    }
}

private fun Modifier.maybeReserveNotesSpace(reserve: Boolean, totalWidth: Dp): Modifier {
    return if(reserve)
        this.widthIn(max = (totalWidth - 55.dp) * 2 / 3)
    else this
}

private val Substitution.hasSubstTeacher: Boolean
    get() { return this.substTeacher.shortName != "##" && this.substTeacher.shortName.isNotEmpty() }

/**
 * Returns the lesson number as a string, with a leading space if it's a single digit,
 * and a trailing dot.
 * @return String Lesson number
 */
private fun Substitution.lessonNrAsString(): String {
    return (if(this.lessonNr.length == 1) " " else "") + this.lessonNr + "."
}

@Composable
private fun Substitution.getCardTitle(): String {
    return stringResource(when {
        this.origSubject == this.substSubject
        -> MR.strings.subplan_samesubject  //Deutsch

        this.type == SubstitutionType.WORKORDER
                && this.origSubject == this.substSubject
        -> MR.strings.subplan_workorder_samesubject  //Deutsch Arbeitsauftrag

        this.type == SubstitutionType.WORKORDER
                && this.origSubject != this.substSubject
        -> MR.strings.subplan_workorder //Deutsch ➜ Mathe Arbeitsauftrag

        this.type == SubstitutionType.CANCELLATION
        -> MR.strings.subplan_cancellation //Deutsch ➜ Ausfall

        this.type == SubstitutionType.BREASTFEED
        -> MR.strings.subplan_breastfeed //Deutsch ➜ Stillbeschäftigung"

        else
        -> MR.strings.subplan_normal //Deutsch ➜ Mathe
    }, this.origSubject.longName, this.substSubject.longName)
}

private fun Substitution.shouldDisplayNotes(): Boolean =
    this.notes.isNotEmpty()
            && this.type != SubstitutionType.BREASTFEED
            && this.type != SubstitutionType.WORKORDER
            && this.type != SubstitutionType.CANCELLATION