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

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionType
import de.xorg.gsapp.ui.materialtools.MaterialColors
import dev.icerock.moko.resources.compose.stringResource
import de.xorg.gsapp.res.MR

@ExperimentalMaterial3Api
@Composable
fun SubstitutionCard(
    value: Substitution
) {
    val harmonizedColor = MaterialColors.harmonize(
        colorToHarmonize = value.origSubject.color.toArgb(),
        colorToHarmonizeWith = MaterialTheme.colorScheme.primary.toArgb())
    val colorRoles = MaterialColors.getColorRoles(color = harmonizedColor,
                                                  isLightTheme = isSystemInDarkTheme())

    var totalWidth by remember { mutableStateOf(64.dp) }

    Card(modifier = Modifier
            .padding(horizontal = 16.dp,
                     vertical = 4.dp)
            .height(70.dp)
            .onGloballyPositioned { coords -> totalWidth = coords.size.width.dp },
        colors = CardDefaults.cardColors(containerColor = Color(colorRoles.accentContainer))) {

        /**** begin Card RowLayout **/
        Row(modifier = Modifier.fillMaxWidth() ) {

            /**** begin LessonNumber | w=55.dp **/
            Box(contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(start = 15.dp,
                             top = 15.dp,
                             bottom = 15.dp)
                    .fillMaxHeight() ) {

                /* LessonNumber -> Circle Background */
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(colorRoles.accent))
                ) {
                    Text(text = (if(value.lessonNr.length == 1) " " else "")
                                + value.lessonNr + ".",
                         style = MaterialTheme.typography.titleMedium,
                         color = Color(colorRoles.onAccent),
                         overflow = TextOverflow.Clip,
                         modifier = Modifier.padding(bottom = 1.5f.dp))
                }
            }
            /** end LessonNumber ****/

            /**** begin ContentBox **/
            Box(contentAlignment = Alignment.CenterStart,
                modifier =  if(value.shouldDisplayNotes()) Modifier
                                .fillMaxHeight()
                                .widthIn(max = (totalWidth - 55.dp) * 2 / 3)
                            else Modifier
                                .fillMaxHeight()
                ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {

                    /**** begin ContentBox -> Title **/
                    Text(text = stringResource(when {
                            value.origSubject == value.substSubject
                            -> MR.strings.subplan_samesubject  //Deutsch

                            value.type == SubstitutionType.WORKORDER
                                    && value.origSubject == value.substSubject
                            -> MR.strings.subplan_workorder_samesubject  //Deutsch Arbeitsauftrag

                            value.type == SubstitutionType.WORKORDER
                                    && value.origSubject != value.substSubject
                            -> MR.strings.subplan_workorder //Deutsch ➜ Mathe Arbeitsauftrag

                            value.type == SubstitutionType.CANCELLATION
                            -> MR.strings.subplan_cancellation //Deutsch ➜ Ausfall

                            value.type == SubstitutionType.BREASTFEED
                            -> MR.strings.subplan_breastfeed //Deutsch ➜ Stillbeschäftigung"

                            else
                            -> MR.strings.subplan_normal //Deutsch ➜ Mathe
                         }, value.origSubject.longName, value.substSubject.longName),
                         maxLines = 1,
                         overflow = TextOverflow.Ellipsis,
                         style = MaterialTheme.typography.titleMedium,
                         modifier = Modifier.padding(start = 4.dp),
                         color = Color(colorRoles.onAccentContainer)
                    ) /** end ContentBox -> Title ****/

                    /**** begin ContentBox --> SubBox **/
                    Row(modifier = Modifier.padding(top = 3.dp)) {

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
                                .alignByBaseline(),
                             softWrap = false,
                             color = Color(colorRoles.onAccentContainer) )

                        /* ContentBox -> SubBox -> Teacher */
                        if(value.substTeacher.shortName != "##" && value.substTeacher.shortName.isNotEmpty()) {
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
                                    .alignByBaseline(),
                                 softWrap = false,
                                 color = Color(colorRoles.onAccentContainer))
                        }
                    } /** end ContentBox -> SubBox ****/

                } /* end ContentBox column */
            } /** end ContentBox ****/

            if(value.shouldDisplayNotes()) {
                /**** begin NotesBox (if there are notes) **/
                Row(horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .defaultMinSize(minWidth = (totalWidth - 55.dp) / 3)
                        .wrapContentSize()
                        .fillMaxHeight()) {

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
                                .wrapContentSize() )
                    }
                } /** end NotesBox ****/
            } /* end if NotesBox */

        } /** end Card RowLayout ****/
    }
}

private fun Substitution.shouldDisplayNotes(): Boolean =
    this.notes.isNotEmpty()
            && this.type != SubstitutionType.BREASTFEED
            && this.type != SubstitutionType.WORKORDER
            && this.type != SubstitutionType.CANCELLATION

