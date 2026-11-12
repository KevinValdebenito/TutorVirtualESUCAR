package cl.duoc.tutorvirtual.data.model

data class PerfilErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val direccion: String? = null,
    val clave: String? = null
)

data class PerfilUiState(
    val nombre: String = "",
    val correo: String = "",
    val direccion: String = "",
    val clave: String = "",
    val rut: String = "",
    val grado: String = "",
    val asignatura: String = "",
    val profesorGuia: String = "",
    val errores: PerfilErrores = PerfilErrores(),
    val guardadoExitoso: Boolean = false,
    val errorMensaje: String = ""
)
