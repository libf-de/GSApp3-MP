/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
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


import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.material.color.MaterialColors
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionDisplay
import de.xorg.gsapp.data.model.SubstitutionType
import de.xorg.gsapp.data.model.Teacher

@ExperimentalMaterial3Api
@Composable
actual fun SubstitutionCard(
    value: SubstitutionDisplay
) {
    val lessonNrStyle = MaterialTheme.typography.titleMedium
    val lineHeightDp: Dp = with(LocalDensity.current) {
        lessonNrStyle.fontSize.toDp()
    }

    val harmonizedColor = MaterialColors.harmonize(
        value.origSubject.color.toArgb(),
        MaterialTheme.colorScheme.primary.toArgb())
    //val harmonizedColor = value.origSubject.color.toArgb()

    //val farbe = MaterialColors.getColorRoles(LocalContext.current, harmonizedColor)
    val farbe = MaterialColors.getColorRoles(harmonizedColor, false)
    Card(modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .height(70.dp),
        colors = CardDefaults
            .cardColors(containerColor = Color(farbe.accentContainer))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(15.dp, 15.dp, 0.dp, 15.dp)
                    .fillMaxHeight()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(farbe.accent))
                ) {
                    Text(" " + value.lessonNr + ".",
                        style = lessonNrStyle,
                        color = Color(farbe.onAccent),
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, (1.5f).dp))
                }
            }


            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxHeight()) {
                Column(modifier = Modifier.padding(12.dp, 0.dp)) {
                    Text(
                        text = when {
                            value.origSubject == value.substSubject
                            -> value.origSubject.longName

                            value.type == SubstitutionType.WORKORDER
                                    && value.origSubject == value.substSubject
                            -> value.origSubject.longName + " Arbeitsauftrag"

                            value.type == SubstitutionType.WORKORDER
                                    && value.origSubject != value.substSubject
                            -> value.origSubject.longName + " ➜ " +
                                    value.substSubject.longName + " Arbeitsauftrag"

                            value.type == SubstitutionType.CANCELLATION
                            -> value.origSubject.longName + " ➜ Ausfall"

                            value.type == SubstitutionType.BREASTFEED
                            -> value.origSubject.longName + " ➜ Stillbeschäftigung"

                            else
                            -> value.origSubject.longName + " ➜ " + value.substSubject.longName
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp),
                        color = Color(farbe.onAccentContainer)
                    )
                    Row(modifier = Modifier.padding(0.dp, 3.dp, 0.dp, 0.dp)) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier
                                .size(24.dp)
                                .alignByBaseline(),
                            tint = Color(farbe.onAccentContainer)
                        )
                        Text(
                            text = value.substRoom,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if(value.type == SubstitutionType.ROOMSWAP)
                                FontWeight.ExtraBold
                            else
                                MaterialTheme.typography.bodyMedium.fontWeight,
                            modifier = Modifier
                                .padding(4.dp, 2.dp, 0.dp, 0.dp)
                                .alignByBaseline(),
                            softWrap = false,
                            color = Color(farbe.onAccentContainer)
                        )
                        if(value.substTeacher.shortName != "##" && value.substTeacher.shortName.isNotEmpty()) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = "Bla!",
                                modifier = Modifier
                                    .padding(8.dp, 0.dp, 0.dp, 0.dp)
                                    .size(24.dp)
                                    .alignByBaseline(),
                                tint = Color(farbe.onAccentContainer)
                            )
                            Text(
                                text = value.substTeacher.longName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(4.dp, 2.dp, 8.dp, 0.dp)
                                    .width(78.dp)
                                    .alignByBaseline(),
                                softWrap = false,
                                color = Color(farbe.onAccentContainer)
                            )
                        }
                    }

                }
            }

            if(value.notes.isNotEmpty()
                && value.type != SubstitutionType.BREASTFEED
                && value.type != SubstitutionType.WORKORDER
                && value.type != SubstitutionType.CANCELLATION) {
                Row(horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()) {
                    Divider(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .padding(0.dp, 10.dp)
                            .fillMaxHeight()  //fill the max height
                            .width(1.dp)
                    )

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = value.notes,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .padding(8.dp, 13.dp, 15.dp, 12.dp)
                                .wrapContentSize()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun normalNoNotesPreview() {
    val sampleSubDisp = SubstitutionDisplay(
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun normalWithNotesPreview() {
    val sampleSubDisp = SubstitutionDisplay(
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun cancellationPreview() {
    SubstitutionCard(value = SubstitutionDisplay(
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun workorderPreview() {
    SubstitutionCard(value = SubstitutionDisplay(
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun roomswapPreview() {
    SubstitutionCard(value = SubstitutionDisplay(
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun breastfeedPreview() {
    SubstitutionCard(value = SubstitutionDisplay(
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