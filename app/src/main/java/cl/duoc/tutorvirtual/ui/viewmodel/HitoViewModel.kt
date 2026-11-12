package cl.duoc.tutorvirtual.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.TutorApplication
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.notificaciones.RecordatoriosHitos
import cl.duoc.tutorvirtual.util.FechasHito
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface EventoAvances {
    data class Mensaje(val texto: String) : EventoAvances
    data object AvanceGuardado : EventoAvances
    data object AvanceEditado : EventoAvances
}

class HitoViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = (application as TutorApplication).container.hitoRepository
    val hitos = repositorio.hitosFlow
    val avances = repositorio.avancesFlow
    val cargando = MutableStateFlow(true)
    val operando = MutableStateFlow(false)
    val eventos = MutableSharedFlow<EventoAvances>()

    init {
        viewModelScope.launch {
            try { repositorio.recargarDatos() }
            catch (_: Exception) { eventos.emit(EventoAvances.Mensaje("No se pudieron cargar los datos")) }
            finally { cargando.value = false }
        }
    }

    private fun ejecutar(exito: String, accion: suspend () -> Boolean, despues: (suspend () -> Unit)? = null) {
        if (operando.value) return
        viewModelScope.launch {
            operando.value = true
            try {
                if (accion()) {
                    if (despues != null) despues()
                    eventos.emit(EventoAvances.Mensaje(exito))
                } else eventos.emit(EventoAvances.Mensaje("No se pudo completar la operación"))
            } catch (_: Exception) {
                eventos.emit(EventoAvances.Mensaje("Ocurrió un error al guardar en el dispositivo"))
            } finally { operando.value = false }
        }
    }

    fun cambiarEstado(id: Long, estado: EstadoHito, porcentaje: Int) {
        ejecutar("Estado del hito actualizado", {
            repositorio.actualizarEstadoHito(id, estado, porcentaje)
        }, {
            repositorio.obtenerHitoPorId(id)?.let {
                RecordatoriosHitos.programar(getApplication(), it)
            }
        })
    }

    fun actualizarFechaLimite(id: Long, fecha: String) {
        val dia = FechasHito.leer(fecha)
        if (dia == null || dia.isBefore(LocalDate.now())) {
            viewModelScope.launch { eventos.emit(EventoAvances.Mensaje("Selecciona una fecha válida desde hoy")) }
            return
        }
        ejecutar("Fecha límite guardada; recordatorio actualizado", {
            repositorio.actualizarFechaLimite(id, fecha)
        }, {
            repositorio.obtenerHitoPorId(id)?.let {
                RecordatoriosHitos.programar(getApplication(), it)
            }
        })
    }

    fun registrarAvance(hitoId: Long, titulo: String, descripcion: String) {
        if (titulo.isBlank() || descripcion.isBlank()) {
            viewModelScope.launch { eventos.emit(EventoAvances.Mensaje("Completa título y descripción")) }
            return
        }
        val avance = Avance(hitoId = hitoId, titulo = titulo.trim(),
            descripcion = descripcion.trim(), fecha = LocalDate.now().toString())
        ejecutar("Avance guardado correctamente", {
            repositorio.registrarAvance(avance) > 0
        }, {
            eventos.emit(EventoAvances.AvanceGuardado)
        })
    }

    fun actualizarAvance(avance: Avance) {
        if (avance.titulo.isBlank() || avance.descripcion.isBlank()) {
            viewModelScope.launch { eventos.emit(EventoAvances.Mensaje("Completa título y descripción")) }
            return
        }
        ejecutar("Avance actualizado correctamente", {
            repositorio.actualizarAvance(avance.copy(
                titulo = avance.titulo.trim(), descripcion = avance.descripcion.trim()))
        }, { eventos.emit(EventoAvances.AvanceEditado) })
    }

    fun eliminarAvance(id: Long) {
        ejecutar("Avance eliminado correctamente", { repositorio.eliminarAvance(id) })
    }
}
