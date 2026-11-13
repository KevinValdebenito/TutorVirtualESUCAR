package cl.duoc.tutorvirtual.data.model

enum class EstadoHito(val label: String) {
    POR_INICIAR("Por iniciar"),
    EN_DESARROLLO("En desarrollo"),
    ENVIADO("Enviado"),
    APROBADO("Aprobado");

    companion object {
        fun fromString(value: String): EstadoHito {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true)
            } ?: POR_INICIAR
        }
    }
}

data class Hito(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val formatoRequerido: String = "Documento PDF bajo norma APA 7ma ed.",
    val fechaLimite: String,
    val estado: EstadoHito = EstadoHito.POR_INICIAR,
    val porcentajeAvance: Int = 0,
    val comentariosDocente: String = "",
    val criteriosEvaluacion: String = ""
)
