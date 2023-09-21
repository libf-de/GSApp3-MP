package de.xorg.gsapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.viewmodels.GSAppViewModel
import de.xorg.gsapp.ui.components.LoadingComponent
import de.xorg.gsapp.ui.components.SubstitutionCard
import de.xorg.gsapp.ui.state.UiState
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.navigation.Navigator
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubstitutionsScreen(
    navController: Navigator
) {
    val di = localDI()

    val viewModel by di.instance<GSAppViewModel>()

    val sds = viewModel.subStateFlow.collectAsState().value
    var isFirst = false

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = stringResource(MR.strings.tab_substitutions),
                        fontFamily = fontFamilyResource(MR.fonts.LondrinaSolid.black),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { navController.navigate(GSAppRoutes.SETTINGS) }) {
                        Icon(imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings") //TODO: Localize!
                    }
                }
            )
        }
    ) { innerPadding ->
        when(viewModel.uiState.substitutionState) {
            UiState.NORMAL -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).padding(bottom = 80.dp)
                ) {
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
            }
            UiState.LOADING -> {
                LoadingComponent(modifier = Modifier.fillMaxSize())
            }
            UiState.EMPTY -> {
                Text("keine Vertretungen")
            }
            UiState.NO_DATASOURCE -> {
                Text("keine Vertretungen")
            }
            else -> {
                Text("Fehler: ${viewModel.uiState.substitutionError.message}")
            }
        }






    }


}