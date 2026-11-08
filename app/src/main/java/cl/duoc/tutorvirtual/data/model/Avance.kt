package cl.duoc.tutorvirtual.data.model

data class Avance(
    val id: Long = 0,
    val hitoId: Long,
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val horasDedicadas: Int = 1,
    val rutaAdjunto: String? = null
)
