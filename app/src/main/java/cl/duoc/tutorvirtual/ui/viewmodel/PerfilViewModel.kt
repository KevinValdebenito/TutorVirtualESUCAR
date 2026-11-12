package cl.duoc.tutorvirtual.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.TutorApplication
import cl.duoc.tutorvirtual.data.model.PerfilErrores
import cl.duoc.tutorvirtual.data.model.PerfilUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        (application as TutorApplication).container.perfilRepository

    private val preferencias = application.getSharedPreferences(
        "datos_perfil",
        Context.MODE_PRIVATE
    )

    private val _estado = MutableStateFlow(PerfilUiState())
    val estado: StateFlow<PerfilUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val perfil = repositorio.obtenerPerfil()
                val dir = preferencias.getString("direccion", "") ?: ""
                _estado.update {
                    it.copy(
                        nombre = perfil.nombre,
                        correo = perfil.email,
                        direccion = dir,
                        rut = perfil.rut,
                        grado = perfil.grado,
                        asignatura = perfil.asignatura,
                        profesorGuia = perfil.profesorGuia
                    )
                }
            } catch (e: Exception) {
                _estado.update { it.copy(errorMensaje = "No se pudo cargar el perfil") }
            }
        }
    }

    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                errores = it.errores.copy(nombre = null),
                guardadoExitoso = false
            )
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                errores = it.errores.copy(correo = null),
                guardadoExitoso = false
            )
        }
    }

    fun onDireccionChange(valor: String) {
        _estado.update {
            it.copy(
                direccion = valor,
                errores = it.errores.copy(direccion = null),
                guardadoExitoso = false
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                clave = valor,
                errores = it.errores.copy(clave = null),
                guardadoExitoso = false
            )
        }
    }

    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val errNombre = if (actual.nombre.isBlank()) "El nombre es obligatorio" else null
        val errCorreo = if (actual.correo.isBlank()) {
            "El correo es obligatorio"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(actual.correo.trim()).matches()) {
            "Ingresa un correo electrónico válido"
        } else null
        val errDireccion = if (actual.direccion.isBlank()) "La dirección es obligatoria" else null
        val errClave = if (actual.clave.length < 6) "Mínimo 6 caracteres" else null

        val nuevosErrores = PerfilErrores(
            nombre = errNombre,
            correo = errCorreo,
            direccion = errDireccion,
            clave = errClave
        )

        _estado.update { it.copy(errores = nuevosErrores) }

        return listOfNotNull(errNombre, errCorreo, errDireccion, errClave).isEmpty()
    }

    fun guardar() {
        if (!validarFormulario()) return

        viewModelScope.launch {
            try {
                val actual = _estado.value
                val perfilActual = repositorio.obtenerPerfil()
                val nuevoPerfil = perfilActual.copy(
                    nombre = actual.nombre.trim(),
                    email = actual.correo.trim()
                )

                val resultado = repositorio.guardarPerfil(nuevoPerfil)

                if (resultado) {
                    val direccionLimpia = actual.direccion.trim()
                    preferencias.edit()
                        .putString("direccion", direccionLimpia)
                        .apply()

                    _estado.update {
                        it.copy(
                            guardadoExitoso = true,
                            errorMensaje = ""
                        )
                    }
                } else {
                    _estado.update { it.copy(errorMensaje = "No se pudo guardar el perfil") }
                }
            } catch (e: Exception) {
                _estado.update { it.copy(errorMensaje = "Ocurrió un error al guardar") }
            }
        }
    }

    fun volverEditar() {
        _estado.update {
            it.copy(
                guardadoExitoso = false,
                clave = "",
                errores = PerfilErrores()
            )
        }
    }
}
