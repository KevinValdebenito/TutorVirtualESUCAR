package cl.duoc.tutorvirtual.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

/** Fechas que ya utiliza la base de datos: dd/MM/yyyy. */
object FechasHito {
    private val formato = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    fun leer(texto: String): LocalDate? = try {
        LocalDate.parse(texto, formato)
    } catch (_: DateTimeParseException) {
        null
    }

    fun mostrar(fecha: LocalDate): String = fecha.format(formato)
}
