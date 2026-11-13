package cl.duoc.tutorvirtual.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.TutorApplication
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.notificaciones.RecordatoriosHitos
import cl.duoc.tutorvirtual.util.FechasHito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FormularioAvanceUiState(
    val hitoId: Long? = null,
    val visible: Boolean = false,
    val titulo: String = "",
    val descripcion: String = "",
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorGeneral: String? = null
)

class HitoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        (application as TutorApplication).container.hitoRepository

    val hitos = repositorio.hitosFlow
    val avances = repositorio.avancesFlow

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _formularioAvance = MutableStateFlow(FormularioAvanceUiState())
    val formularioAvance: StateFlow<FormularioAvanceUiState> =
        _formularioAvance.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repositorio.recargarDatos()
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cambiarEstado(
        id: Long,
        estado: EstadoHito,
        porcentaje: Int
    ) {
        viewModelScope.launch {
            val actualizado = repositorio.actualizarEstadoHito(
                id,
                estado,
                porcentaje
            )
            if (actualizado) {
                repositorio.obtenerHitoPorId(id)?.let {
                    RecordatoriosHitos.programar(getApplication<Application>(), it)
                }
            }
        }
    }

    fun actualizarFechaLimite(id: Long, fecha: String) {
        val fechaElegida = FechasHito.leer(fecha)
        if (fechaElegida == null || fechaElegida.isBefore(LocalDate.now())) return

        viewModelScope.launch {
            val actualizado = repositorio.actualizarFechaLimite(id, fecha)
            if (actualizado) {
                repositorio.obtenerHitoPorId(id)?.let {
                    RecordatoriosHitos.programar(getApplication<Application>(), it)
                }
            }
        }
    }

    fun alternarFormularioAvance(hitoId: Long) {
        val actual = _formularioAvance.value
        _formularioAvance.value = if (actual.visible && actual.hitoId == hitoId) {
            FormularioAvanceUiState()
        } else {
            FormularioAvanceUiState(hitoId = hitoId, visible = true)
        }
    }

    fun onTituloAvanceChange(valor: String) {
        _formularioAvance.update {
            it.copy(titulo = valor, errorTitulo = null, errorGeneral = null)
        }
    }

    fun onDescripcionAvanceChange(valor: String) {
        _formularioAvance.update {
            it.copy(descripcion = valor, errorDescripcion = null, errorGeneral = null)
        }
    }

    fun registrarAvance() {
        val formulario = _formularioAvance.value
        val errorTitulo = if (formulario.titulo.isBlank()) "El título es obligatorio" else null
        val errorDescripcion =
            if (formulario.descripcion.isBlank()) "La descripción es obligatoria" else null
        val hitoId = formulario.hitoId

        if (errorTitulo != null || errorDescripcion != null || hitoId == null) {
            _formularioAvance.update {
                it.copy(
                    errorTitulo = errorTitulo,
                    errorDescripcion = errorDescripcion,
                    errorGeneral = if (hitoId == null) "No se pudo identificar el hito" else null
                )
            }
            return
        }

        val avance = Avance(
            hitoId = hitoId,
            titulo = formulario.titulo.trim(),
            descripcion = formulario.descripcion.trim(),
            fecha = FechasHito.mostrar(LocalDate.now())
        )

        viewModelScope.launch {
            val resultado = repositorio.registrarAvance(avance)
            _formularioAvance.value = if (resultado > 0) {
                FormularioAvanceUiState()
            } else {
                formulario.copy(errorGeneral = "No se pudo guardar el avance")
            }
        }
    }

    fun eliminarAvance(id: Long) {
        viewModelScope.launch {
            repositorio.eliminarAvance(id)
        }
    }
}
