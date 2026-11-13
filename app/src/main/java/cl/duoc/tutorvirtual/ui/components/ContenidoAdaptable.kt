package cl.duoc.tutorvirtual.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Centra el contenido en pantallas anchas sin afectar el ancho disponible en teléfonos. */
@Composable
fun ContenidoAdaptable(
    modifier: Modifier = Modifier,
    anchoMaximo: Dp = 900.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = anchoMaximo)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}
