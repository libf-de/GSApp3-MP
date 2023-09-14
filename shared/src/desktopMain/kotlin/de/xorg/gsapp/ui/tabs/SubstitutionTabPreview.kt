package de.xorg.gsapp.ui.tabs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.ui.GSAppViewModel
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import org.kodein.di.singleton


@Composable
@Preview
fun substitutionTabPreview() = withDI({
    bind<GSAppRepository>() with singleton { MockAppRepository() }
    bind<GSAppViewModel>() with singleton { GSAppViewModel(instance()) }
}) {
    SubstitutionsTab()
}