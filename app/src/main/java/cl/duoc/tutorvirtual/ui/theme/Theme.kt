package cl.duoc.tutorvirtual.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VerdeModoOscuro,
    onPrimary = SuperficieClara,
    primaryContainer = VerdeInstitucionalOscuro,
    onPrimaryContainer = VerdeClaroModoOscuro,
    secondary = VerdeClaroModoOscuro,
    onSecondary = VerdeInstitucionalOscuro,
    secondaryContainer = VerdeSecundario,
    onSecondaryContainer = SuperficieClara,
    tertiary = DoradoInstitucional,
    background = FondoOscuro,
    onBackground = TextoClaro,
    surface = SuperficieOscura,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieOscuraVariante,
    outline = BordeSuave,
    error = ErrorInstitucional
)

private val LightColorScheme = lightColorScheme(
    primary = VerdeInstitucional,
    onPrimary = SuperficieClara,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeInstitucionalOscuro,
    secondary = VerdeSecundario,
    onSecondary = SuperficieClara,
    secondaryContainer = VerdeMuyClaro,
    onSecondaryContainer = VerdeInstitucionalOscuro,
    tertiary = DoradoInstitucional,
    background = FondoCalido,
    onBackground = TextoOscuro,
    surface = SuperficieClara,
    onSurface = TextoOscuro,
    surfaceVariant = VerdeMuyClaro,
    outline = BordeSuave,
    error = ErrorInstitucional
)

@Composable
fun TutorVirtualESUCARTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // No se usa color dinámico: así la identidad visual se mantiene en todos los equipos.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
