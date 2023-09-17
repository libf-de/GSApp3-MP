package de.xorg.gsapp.ui.tabs

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.GSAppViewModel
import de.xorg.gsapp.ui.components.LoadingComponent
import de.xorg.gsapp.ui.components.SubstitutionCard
import de.xorg.gsapp.ui.state.UiState
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instance

//This should be an object, but to fix a "no implementation for FUN MISSING_DECLARATION" internal
//class is used (https://github.com/JetBrains/compose-multiplatform/issues/3444)
@OptIn(ExperimentalMaterial3Api::class)
internal class SubstitutionsTab : Tab {


    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.tab_substitutions)
            val icon = painterResource(MR.images.substitutions)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )

            }
        }

    @Composable
    override fun Content() {
        val di = localDI()

        val viewModel by di.instance<GSAppViewModel>()

        val sds = viewModel.subStateFlow.collectAsState().value
        var isFirst = true

        LazyColumn(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 84.dp)
        ) {
            item {
                Text(
                    text = options.title,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            when(viewModel.uiState.substitutionState) {
                UiState.NORMAL -> {
                    sds.substitutions.forEach {
                        item {
                            Text(
                                text = it.key,
                                modifier = Modifier.padding(
                                    start = 28.dp,
                                    top = if(!isFirst) 12.dp else 0.dp,
                                    end = 0.dp,
                                    bottom = 0.dp
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(it.value) { substitution ->
                            SubstitutionCard(value = substitution)
                        }
                        isFirst = false
                    }
                }
                UiState.LOADING -> {
                    item {
                        LoadingComponent(modifier = Modifier.fillMaxHeight())
                    }
                }
                UiState.EMPTY -> {
                    item {
                        Text("keine Vertretungen")
                    }
                }
                UiState.NO_DATASOURCE -> {
                    item {
                        Text("Fehler: Keine Datenquelle :(")
                    }
                }
                else -> {
                    item {
                        Text("Fehler: ${viewModel.uiState.substitutionError.message}")
                    }
                }
            }
        }
    }
}