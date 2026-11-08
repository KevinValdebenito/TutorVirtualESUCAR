package cl.duoc.tutorvirtual.data.repository

import cl.duoc.tutorvirtual.data.local.FaqLocalDataSource
import cl.duoc.tutorvirtual.data.model.PreguntaFrecuente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FaqRepository {
    suspend fun obtenerFaqs(categoria: String? = null, query: String? = null): List<PreguntaFrecuente>
    suspend fun obtenerCategorias(): List<String>
}

class FaqRepositoryImpl(
    private val localDataSource: FaqLocalDataSource
) : FaqRepository {

    override suspend fun obtenerFaqs(categoria: String?, query: String?): List<PreguntaFrecuente> =
        withContext(Dispatchers.IO) {
            localDataSource.obtenerFaqs(categoria, query)
        }

    override suspend fun obtenerCategorias(): List<String> =
        withContext(Dispatchers.IO) {
            localDataSource.obtenerCategorias()
        }
}
