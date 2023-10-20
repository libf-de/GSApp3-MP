package de.xorg.gsapp.data.sources.defaults

import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.data.model.Subject
import org.kodein.di.DI

class GsDefaultsSource(di: DI) : DefaultsDataSource {


override fun getDefaultSubjects(): List<Subject> {
        return listOf(
            Subject("De", "Deutsch", Color(0xFF2196F3)),
            Subject("Ma", "Mathe", Color(0xFFF44336)),
            Subject("Mu", "Musik", Color(0xFF9E9E9E)),
            Subject("Ku", "Kunst", Color(0xFF673AB7)),
            Subject("Gg", "Geografie", Color(0xFF9E9D24)),
            Subject("Re", "Religion", Color(0xFFFF8F00)),
            Subject("Et", "Ethik", Color(0xFFFF8F00)),
            Subject("MNT", "MNT", Color(0xFF4CAF50)),
            Subject("En", "Englisch", Color(0xFFFF9800)),
            Subject("Sp", "Sport", Color(0xFF607D8B)),
            Subject("SpJ", "Sport Jungen", Color(0xFF607D8B)),
            Subject("SpM", "Sport Mädchen", Color(0xFF607D8B)),
            Subject("Bi", "Biologie", Color(0xFF4CAF50)),
            Subject("Ch", "Chemie", Color(0xFFE91E63)),
            Subject("Ph", "Physik", Color(0xFF009688)),
            Subject("Sk", "Sozialkunde", Color(0xFF795548)),
            Subject("If", "Informatik", Color(0xFF03A9F4)),
            Subject("WR", "Wirtschaft/Recht", Color(0xFFFF5722)),
            Subject("Ge", "Geschichte", Color(0xFF9C27B0)),
            Subject("Fr", "Französisch", Color(0xFF558B2F)),
            Subject("Ru", "Russisch", Color(0xFF558B2F)),
            Subject("La", "Latein", Color(0xFF558B2F)),
            Subject("Gewi", "Gesellschaftsw.", Color(0xFF795548)),
            Subject("Dg", "Darstellen/Gestalten", Color(0xFF795548)),
            Subject("Sn", "Spanisch", Color(0xFF558B2F)),
            Subject("&nbsp;", "keine Angabe", Color.DarkGray)
        )
    }
}