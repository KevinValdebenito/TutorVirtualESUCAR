package cl.duoc.tutorvirtual.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = VerdeInstitucional,
    onPrimary = Blanco,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeProfundo,

    secondary = VerdeIntermedio,
    onSecondary = Blanco,
    secondaryContainer = VerdeClaro,
    onSecondaryContainer = VerdeProfundo,

    tertiary = GrisVerde,
    onTertiary = Blanco,

    background = FondoClaro,
    onBackground = TextoOscuro,

    surface = Blanco,
    onSurface = TextoOscuro,

    surfaceVariant = VerdeClaro,
    onSurfaceVariant = VerdeProfundo,

    outline = GrisVerde
)

private val DarkColorScheme = darkColorScheme(
    primary = ColorVerdeModoOscuro,
    onPrimary = VerdeProfundo,
    primaryContainer = VerdeInstitucional,
    onPrimaryContainer = Blanco,

    secondary = ColorVerdeModoOscuro,
    onSecondary = VerdeProfundo,

    tertiary = VerdeClaro,
    onTertiary = VerdeProfundo,

    background = FondoOscuro,
    onBackground = Blanco,

    surface = SuperficieOscura,
    onSurface = Blanco,

    surfaceVariant = VerdeProfundo,
    onSurfaceVariant = VerdeClaro,

    outline = VerdeClaro
)

@Composable
fun TutorVirtualESUCARTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        },
        typography = Typography,
        content = content
    )
}