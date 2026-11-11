
package cl.duoc.tutorvirtual.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.TutorApplication
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HitoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        (application as TutorApplication).container.hitoRepository

    val hitos = repositorio.hitosFlow
    val avances = repositorio.avancesFlow

    val cargando = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            try {
                repositorio.recargarDatos()
            } finally {
                cargando.value = false
            }
        }
    }

    fun cambiarEstado(
        id: Long,
        estado: EstadoHito,
        porcentaje: Int
    ) {
        viewModelScope.launch {
            repositorio.actualizarEstadoHito(
                id,
                estado,
                porcentaje
            )
        }
    }

    fun registrarAvance(
        hitoId: Long,
        titulo: String,
        descripcion: String
    ) {
        if (titulo.isBlank() || descripcion.isBlank()) {
            return
        }

        val avance = Avance(
            hitoId = hitoId,
            titulo = titulo.trim(),
            descripcion = descripcion.trim(),
            fecha = LocalDate.now().toString()
        )

        viewModelScope.launch {
            repositorio.registrarAvance(avance)
        }
    }

    fun eliminarAvance(id: Long) {
        viewModelScope.launch {
            repositorio.eliminarAvance(id)
        }
    }
}
