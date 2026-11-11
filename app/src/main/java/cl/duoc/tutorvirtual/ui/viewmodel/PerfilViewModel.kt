
package cl.duoc.tutorvirtual.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.TutorApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        (application as TutorApplication).container.perfilRepository

    private val preferencias = application.getSharedPreferences(
        "datos_perfil",
        Context.MODE_PRIVATE
    )

    val perfil = repositorio.perfilFlow

    val direccion = MutableStateFlow(
        preferencias.getString("direccion", "") ?: ""
    )

    val guardado = MutableStateFlow(false)
    val error = MutableStateFlow("")

    init {
        viewModelScope.launch {
            try {
                repositorio.obtenerPerfil()
            } catch (e: Exception) {
                error.value = "No se pudo cargar el perfil"
            }
        }
    }

    fun guardar(
        nombre: String,
        correo: String,
        nuevaDireccion: String
    ) {
        viewModelScope.launch {

            guardado.value = false
            error.value = ""

            try {
                val nuevoPerfil = perfil.value.copy(
                    nombre = nombre.trim(),
                    email = correo.trim()
                )

                val resultado = repositorio.guardarPerfil(nuevoPerfil)

                if (resultado) {

                    val direccionLimpia = nuevaDireccion.trim()

                    preferencias.edit()
                        .putString("direccion", direccionLimpia)
                        .apply()

                    direccion.value = direccionLimpia
                    guardado.value = true

                } else {
                    error.value = "No se pudo guardar el perfil"
                }

            } catch (e: Exception) {
                error.value = "Ocurrió un error al guardar"
            }
        }
    }

    fun volverEditar() {
        guardado.value = false
    }
}
