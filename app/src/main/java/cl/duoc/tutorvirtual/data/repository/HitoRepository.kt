package cl.duoc.tutorvirtual.data.repository

import cl.duoc.tutorvirtual.data.local.HitoLocalDataSource
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.data.model.Hito
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

interface HitoRepository {
    val hitosFlow: StateFlow<List<Hito>>
    val avancesFlow: StateFlow<List<Avance>>

    suspend fun recargarDatos()
    suspend fun obtenerHitos(): List<Hito>
    suspend fun obtenerHitoPorId(id: Long): Hito?
    suspend fun actualizarEstadoHito(id: Long, nuevoEstado: EstadoHito, nuevoPorcentaje: Int): Boolean
    suspend fun actualizarFechaLimite(id: Long, fecha: String): Boolean
    suspend fun actualizarAvance(avance: Avance): Boolean
    suspend fun registrarAvance(avance: Avance): Long
    suspend fun obtenerAvancesPorHito(hitoId: Long): List<Avance>
    suspend fun eliminarAvance(id: Long): Boolean
}

class HitoRepositoryImpl(
    private val localDataSource: HitoLocalDataSource
) : HitoRepository {

    private val _hitosFlow = MutableStateFlow<List<Hito>>(emptyList())
    override val hitosFlow: StateFlow<List<Hito>> = _hitosFlow.asStateFlow()

    private val _avancesFlow = MutableStateFlow<List<Avance>>(emptyList())
    override val avancesFlow: StateFlow<List<Avance>> = _avancesFlow.asStateFlow()

    init {
        // Carga inicial
        _hitosFlow.value = localDataSource.obtenerHitos()
        _avancesFlow.value = localDataSource.obtenerTodosLosAvances()
    }

    override suspend fun recargarDatos() = withContext(Dispatchers.IO) {
        _hitosFlow.value = localDataSource.obtenerHitos()
        _avancesFlow.value = localDataSource.obtenerTodosLosAvances()
    }

    override suspend fun obtenerHitos(): List<Hito> = withContext(Dispatchers.IO) {
        val hitos = localDataSource.obtenerHitos()
        _hitosFlow.value = hitos
        hitos
    }

    override suspend fun obtenerHitoPorId(id: Long): Hito? = withContext(Dispatchers.IO) {
        localDataSource.obtenerHitoPorId(id)
    }

    override suspend fun actualizarEstadoHito(id: Long, nuevoEstado: EstadoHito, nuevoPorcentaje: Int): Boolean = withContext(Dispatchers.IO) {
        val actualizado = localDataSource.actualizarEstadoHito(id, nuevoEstado, nuevoPorcentaje)
        if (actualizado) {
            _hitosFlow.value = localDataSource.obtenerHitos()
        }
        actualizado
    }

    override suspend fun actualizarFechaLimite(id: Long, fecha: String): Boolean = withContext(Dispatchers.IO) {
        val actualizado = localDataSource.actualizarFechaLimite(id, fecha)
        if (actualizado) _hitosFlow.value = localDataSource.obtenerHitos()
        actualizado
    }

    override suspend fun actualizarAvance(avance: Avance): Boolean = withContext(Dispatchers.IO) {
        val actualizado = localDataSource.actualizarAvance(avance)
        if (actualizado) _avancesFlow.value = localDataSource.obtenerTodosLosAvances()
        actualizado
    }

    override suspend fun registrarAvance(avance: Avance): Long = withContext(Dispatchers.IO) {
        val id = localDataSource.agregarAvance(avance)
        if (id > 0) {
            _avancesFlow.value = localDataSource.obtenerTodosLosAvances()
            // Si el avance se registra exitosamente y el hito estaba POR_INICIAR, cambiar a EN_DESARROLLO
            val hito = localDataSource.obtenerHitoPorId(avance.hitoId)
            if (hito != null && hito.estado == EstadoHito.POR_INICIAR) {
                localDataSource.actualizarEstadoHito(avance.hitoId, EstadoHito.EN_DESARROLLO, 25)
                _hitosFlow.value = localDataSource.obtenerHitos()
            }
        }
        id
    }

    override suspend fun obtenerAvancesPorHito(hitoId: Long): List<Avance> = withContext(Dispatchers.IO) {
        localDataSource.obtenerAvancesPorHito(hitoId)
    }

    override suspend fun eliminarAvance(id: Long): Boolean = withContext(Dispatchers.IO) {
        val eliminado = localDataSource.eliminarAvance(id)
        if (eliminado) {
            _avancesFlow.value = localDataSource.obtenerTodosLosAvances()
        }
        eliminado
    }
}
