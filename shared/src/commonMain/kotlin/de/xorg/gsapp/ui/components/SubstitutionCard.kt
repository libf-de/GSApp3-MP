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

import androidx.compose.animation.core.animateIntOffsetAsState
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionType
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.materialtools.ColorRoles
import de.xorg.gsapp.ui.materialtools.MaterialColors
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

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

    var totalWidth by remember { mutableStateOf(64.dp) }
    var isExpanded by remember { mutableStateOf(false) }

    var smallRoomPosition by remember { mutableStateOf(Offset.Zero) }
    var largeRoomPosition by remember { mutableStateOf(Offset.Zero) }
    val roomOffset = animateIntOffsetAsState(
        targetValue = if (isExpanded && largeRoomPosition.x != 0f) {
            IntOffset(
                (largeRoomPosition.x - smallRoomPosition.x).toInt(),
                (largeRoomPosition.y - smallRoomPosition.y).toInt()
            )
        } else {
            IntOffset.Zero
        },
        label = "offset"
    )

    var smallTeacherPosition by remember { mutableStateOf(Offset.Zero) }
    var largeTeacherPosition by remember { mutableStateOf(Offset.Zero) }
    val teacherOffset = animateIntOffsetAsState(
        targetValue = if (isExpanded && false) {
            IntOffset(
                (largeTeacherPosition.x - smallTeacherPosition.x).toInt(),
                (largeTeacherPosition.y - smallTeacherPosition.y).toInt()
            )
        } else {
            IntOffset.Zero
        },
        label = "offset"
    )

    var smallNotesPosition by remember { mutableStateOf(Offset.Zero) }
    var largeNotesPosition by remember { mutableStateOf(Offset.Zero) }
    val notesOffset = animateIntOffsetAsState(
        targetValue = if (isExpanded && largeNotesPosition.x != 0f) {
            IntOffset(
                (largeNotesPosition.x - smallNotesPosition.x).toInt(),
                (largeNotesPosition.y - smallNotesPosition.y).toInt()
            )
        } else {
            IntOffset.Zero
        },
        label = "offset"
    )

    Card(modifier = modifier
        .padding(vertical = 4.dp)
        .height(if(!isExpanded) 70.dp else Dp.Unspecified)
        .clickable { isExpanded = !isExpanded }
        .onGloballyPositioned { coords -> totalWidth = coords.size.width.dp },
        colors = CardDefaults.cardColors(containerColor = Color(colorRoles.accentContainer))) {

        /**** begin Card RowLayout **/
        Row(modifier = Modifier.fillMaxWidth() ) {
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

            Box {
                SmallContentBox(
                    value = value,
                    totalWidth = totalWidth,
                    colorRoles = colorRoles,
                    onRoomPositioned = { room ->
                        smallRoomPosition = room
                    },
                    onTeacherPositioned = { teacher ->
                        smallTeacherPosition = teacher
                    },
                    onNotesPositioned = { notes ->
                        smallNotesPosition = notes
                    },
                    roomOffsetState = roomOffset,
                    teacherOffsetState = teacherOffset,
                    notesOffsetState = notesOffset,
                    modifier = Modifier.alpha(if(isExpanded) 0.5f else 1f)
                )

                LargeContentBox(
                    value = value,
                    colorRoles = colorRoles,
                    modifier = Modifier.alpha(if(isExpanded) 0.2f else 0f),
                    onRoomPositioned = { room ->
                        largeRoomPosition = room
                    },
                    onTeacherPositioned = { teacher ->
                        largeTeacherPosition = teacher
                    },
                    onNotesPositioned = { notes ->
                        largeNotesPosition = notes
                    }
                )
            }

        } /** end Card RowLayout ****/
    }
}

@Composable
private fun LargeContentBox(
    value: Substitution,
    colorRoles: ColorRoles,
    modifier: Modifier = Modifier,
    onRoomPositioned: (room: Offset) -> Unit = {},
    onTeacherPositioned: (teacher: Offset) -> Unit = {},
    onNotesPositioned: (notes: Offset) -> Unit = {}
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
                modifier = Modifier.alignByBaseline(),
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
                modifier = Modifier.alignByBaseline(),
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
                modifier = Modifier.alignByBaseline(),
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
                modifier = Modifier.alignByBaseline().onGloballyPositioned {
                    onRoomPositioned(it.positionInRoot())
                },
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
                        .alignByBaseline()
                        .onGloballyPositioned {
                                              onTeacherPositioned(it.positionInRoot())
                        },
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
                    modifier = Modifier.alignByBaseline().onGloballyPositioned { onNotesPositioned(it.positionInRoot()) },
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
    roomOffsetState: State<IntOffset>,
    teacherOffsetState: State<IntOffset>,
    notesOffsetState: State<IntOffset>,
    modifier: Modifier = Modifier,
    onRoomPositioned: (room: Offset) -> Unit,
    onTeacherPositioned: (teacher: Offset) -> Unit,
    onNotesPositioned: (notes: Offset) -> Unit = {}
) {
    Row(modifier = modifier) {
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
                SubBox(
                    value = value,
                    colorRoles = colorRoles,
                    modifier = Modifier.padding(top = 3.dp),
                    onRoomPositioned = onRoomPositioned,
                    onTeacherPositioned = onTeacherPositioned,
                    roomOffsetState = roomOffsetState,
                    teacherOffsetState = teacherOffsetState
                )
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
                onPositioned = onNotesPositioned,
                offsetState = notesOffsetState
            )
        }
    }
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
fun SubBox(
    value: Substitution,
    colorRoles: ColorRoles,
    roomOffsetState: State<IntOffset>,
    teacherOffsetState: State<IntOffset>,
    modifier: Modifier = Modifier,
    onRoomPositioned: (room: Offset) -> Unit = {},
    onTeacherPositioned: (teacher: Offset) -> Unit = {}
) {
    Row(modifier = modifier) {

        /* ContentBox -> SubBox -> Room */
        Icon(imageVector = Icons.Outlined.LocationOn,
            contentDescription = stringResource(MR.strings.subplan_dsc_location),
            modifier = Modifier
                .size(24.dp)
                .alignByBaseline(),
            tint = Color(colorRoles.onAccentContainer)
        )
        Text(text = value.substRoom,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if(value.type == SubstitutionType.ROOMSWAP)
                FontWeight.ExtraBold
            else
                MaterialTheme.typography.bodyMedium.fontWeight,
            modifier = Modifier
                .padding(start = 4.dp,
                    top = 2.dp)
                .alignByBaseline()
                .onGloballyPositioned { coords ->
                    onRoomPositioned(coords.positionInRoot())
                }
                .offset { roomOffsetState.value },
            softWrap = false,
            color = Color(colorRoles.onAccentContainer) )

        /* ContentBox -> SubBox -> Teacher */
        if(value.hasSubstTeacher) {
            Icon(imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(MR.strings.subplan_dsc_teacher),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
                    .alignByBaseline(),
                tint = Color(colorRoles.onAccentContainer))

            Text(text = value.substTeacher.longName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 4.dp,
                        top = 2.dp,
                        end = 8.dp)
                    .wrapContentWidth()
                    .alignByBaseline()
                    .onGloballyPositioned { coords ->
                        onTeacherPositioned(coords.positionInRoot())
                    }
                    .offset {
                            teacherOffsetState.value
                    },
                softWrap = false,
                color = Color(colorRoles.onAccentContainer))
        }
    }
}

@Composable
fun NotesBox(
    value: Substitution,
    colorRoles: ColorRoles,
    offsetState: State<IntOffset>,
    modifier: Modifier = Modifier,
    onPositioned: (notes: Offset) -> Unit = {}
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
                    .onGloballyPositioned { coords ->
                        onPositioned(coords.positionInRoot())
                    }
                    .offset {
                        offsetState.value
                    }
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


private data class SubOffsets(
    var origSub: Offset,
    var substSub: Offset,
    var room: Offset,
    var teacher: Offset,
    var notes: Offset
)