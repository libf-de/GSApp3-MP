package de.xorg.gsapp.ui.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import de.xorg.gsapp.ui.GSAppViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import de.xorg.gsapp.res.MR

@OptIn(ExperimentalMaterial3Api::class)
object FoodplanTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.tab_foodplan)
            val icon = rememberVectorPainter(Icons.Filled.Restaurant)

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

        Text("Food plan here :)")
    }
}