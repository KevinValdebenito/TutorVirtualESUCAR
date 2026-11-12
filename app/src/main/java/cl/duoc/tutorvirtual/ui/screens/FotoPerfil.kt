
package cl.duoc.tutorvirtual.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

class FotoFileProvider : FileProvider()

@Composable
fun FotoPerfil() {

    val context = LocalContext.current

    val preferencias = remember {
        context.getSharedPreferences(
            "foto_perfil",
            Context.MODE_PRIVATE
        )
    }

    var rutaFoto by remember {
        mutableStateOf(
            preferencias.getString("foto", "") ?: ""
        )
    }

    var rutaTemporal by remember {
        mutableStateOf("")
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {

            try {
                val carpeta = File(context.filesDir, "fotos")
                carpeta.mkdirs()

                val archivo = File(
                    carpeta,
                    "galeria_${System.currentTimeMillis()}.jpg"
                )

                val entrada = context.contentResolver
                    .openInputStream(uri)

                if (entrada != null) {

                    entrada.use { origen ->
                        archivo.outputStream().use { destino ->
                            origen.copyTo(destino)
                        }
                    }

                    preferencias.edit()
                        .putString("foto", archivo.absolutePath)
                        .apply()

                    rutaFoto = archivo.absolutePath
                    mensaje = "Foto seleccionada correctamente"

                } else {
                    mensaje = "No se pudo abrir la imagen"
                }

            } catch (e: Exception) {
                mensaje = "Error al seleccionar la imagen"
            }

        } else {
            mensaje = "Selección cancelada"
        }
    }

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { resultado ->

        if (resultado &&
            rutaTemporal.isNotEmpty() &&
            File(rutaTemporal).length() > 0
        ) {

            preferencias.edit()
                .putString("foto", rutaTemporal)
                .apply()

            rutaFoto = rutaTemporal
            mensaje = "Foto tomada correctamente"

        } else {

            if (rutaTemporal.isNotEmpty()) {
                File(rutaTemporal).delete()
            }

            mensaje = "Captura cancelada o fallida"
        }

        rutaTemporal = ""
    }

    val imagen = remember(rutaFoto) {

        if (rutaFoto.isNotEmpty()) {

            val opciones = BitmapFactory.Options().apply {
                inSampleSize = 4
            }

            BitmapFactory.decodeFile(
                rutaFoto,
                opciones
            )?.asImageBitmap()

        } else {
            null
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (imagen != null) {

            Image(
                bitmap = imagen,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        } else {

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("Sin foto")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = {
                    try {

                        val carpeta = File(
                            context.filesDir,
                            "fotos"
                        )

                        carpeta.mkdirs()

                        val archivo = File(
                            carpeta,
                            "camara_${System.currentTimeMillis()}.jpg"
                        )

                        rutaTemporal = archivo.absolutePath

                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            archivo
                        )

                        camara.launch(uri)

                    } catch (e: Exception) {
                        rutaTemporal = ""
                        mensaje = "No se pudo abrir la cámara"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Tomar foto")
            }

            OutlinedButton(
                onClick = {
                    galeria.launch("image/*")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Galería")
            }
        }

        AnimatedVisibility(
            visible = mensaje.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
