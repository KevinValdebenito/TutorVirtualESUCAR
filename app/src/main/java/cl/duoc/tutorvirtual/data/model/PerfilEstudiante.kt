package cl.duoc.tutorvirtual.data.model

data class PerfilEstudiante(
    val nombre: String = "Sargento 2° Rodrigo Morales",
    val rut: String = "18.345.678-9",
    val grado: String = "Sargento 2°",
    val email: String = "rodrigo.morales@esucar.cl",
    val asignatura: String = "Trabajo de Título II",
    val temaTesis: String = "Optimización del patrullaje preventivo mediante análisis georreferenciado",
    val profesorGuia: String = "Prof. Nelson Melo"
)
