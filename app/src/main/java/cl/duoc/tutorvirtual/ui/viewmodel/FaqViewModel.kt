package cl.duoc.tutorvirtual.ui.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.TutorApplication
import cl.duoc.tutorvirtual.data.model.PreguntaFrecuente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FaqViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        (application as TutorApplication).container.faqRepository

    val preguntas = MutableStateFlow<List<PreguntaFrecuente>>(emptyList())
    val categorias = MutableStateFlow<List<String>>(emptyList())
    val cargando = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            categorias.value = repositorio.obtenerCategorias()
            preguntas.value = repositorio.obtenerFaqs()
            cargando.value = false
        }
    }

    fun buscar(texto: String, categoria: String) {

        viewModelScope.launch {

            cargando.value = true

            preguntas.value = repositorio.obtenerFaqs(
                categoria = categoria,
                query = texto
            )

            cargando.value = false
        }
    }
}
