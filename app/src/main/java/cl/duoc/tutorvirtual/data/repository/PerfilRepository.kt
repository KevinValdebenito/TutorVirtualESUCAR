package cl.duoc.tutorvirtual.data.repository

import cl.duoc.tutorvirtual.data.local.PerfilLocalDataSource
import cl.duoc.tutorvirtual.data.model.PerfilEstudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

interface PerfilRepository {
    val perfilFlow: StateFlow<PerfilEstudiante>
    suspend fun obtenerPerfil(): PerfilEstudiante
    suspend fun guardarPerfil(perfil: PerfilEstudiante): Boolean
}

class PerfilRepositoryImpl(
    private val localDataSource: PerfilLocalDataSource
) : PerfilRepository {

    private val _perfilFlow = MutableStateFlow(localDataSource.obtenerPerfil())
    override val perfilFlow: StateFlow<PerfilEstudiante> = _perfilFlow.asStateFlow()

    override suspend fun obtenerPerfil(): PerfilEstudiante = withContext(Dispatchers.IO) {
        val p = localDataSource.obtenerPerfil()
        _perfilFlow.value = p
        p
    }

    override suspend fun guardarPerfil(perfil: PerfilEstudiante): Boolean = withContext(Dispatchers.IO) {
        val guardado = localDataSource.guardarPerfil(perfil)
        if (guardado) {
            _perfilFlow.value = perfil
        }
        guardado
    }
}
