package cl.duoc.tutorvirtual

import cl.duoc.tutorvirtual.util.FechasHito
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class FechasHitoTest {
    @Test fun fechasValidasEInvalidas() {
        assertEquals(LocalDate.of(2026, 11, 30), FechasHito.leer("30/11/2026"))
        assertEquals("05/01/2027", FechasHito.mostrar(LocalDate.of(2027, 1, 5)))
        assertNull(FechasHito.leer("31/02/2027"))
        assertNull(FechasHito.leer("no-es-fecha"))
    }
}
