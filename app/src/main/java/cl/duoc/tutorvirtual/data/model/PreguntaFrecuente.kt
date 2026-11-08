package cl.duoc.tutorvirtual.data.model

data class PreguntaFrecuente(
    val id: Long = 0,
    val categoria: String,
    val pregunta: String,
    val respuesta: String
)
