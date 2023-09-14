package de.xorg.gsapp.ui.tabs

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.GSAppViewModel
import de.xorg.gsapp.ui.components.SubstitutionCard
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
            val icon = rememberVectorPainter(Icons.Filled.School)

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

        LazyColumn(
            modifier = Modifier
        ) {
            items(sds.substitutions) { substitution ->
                SubstitutionCard(value = substitution)
            }
        }
    }
}