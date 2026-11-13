package cl.duoc.tutorvirtual.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.TutorApplication
import cl.duoc.tutorvirtual.data.model.PreguntaFrecuente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FaqViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        (application as TutorApplication).container.faqRepository

    private val _preguntas = MutableStateFlow<List<PreguntaFrecuente>>(emptyList())
    val preguntas: StateFlow<List<PreguntaFrecuente>> = _preguntas.asStateFlow()

    private val _categorias = MutableStateFlow<List<String>>(emptyList())
    val categorias: StateFlow<List<String>> = _categorias.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        viewModelScope.launch {
            _categorias.value = repositorio.obtenerCategorias()
            _preguntas.value = repositorio.obtenerFaqs()
            _cargando.value = false
        }
    }

    fun buscar(texto: String, categoria: String) {

        viewModelScope.launch {

            _cargando.value = true

            _preguntas.value = repositorio.obtenerFaqs(
                categoria = categoria,
                query = texto
            )

            _cargando.value = false
        }
    }
}
