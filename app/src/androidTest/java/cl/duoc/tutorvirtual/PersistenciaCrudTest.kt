package cl.duoc.tutorvirtual

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cl.duoc.tutorvirtual.data.local.HitoLocalDataSource
import cl.duoc.tutorvirtual.data.local.TutorDbHelper
import cl.duoc.tutorvirtual.data.model.Avance
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** No toca la base de datos normal del estudiante. */
@RunWith(AndroidJUnit4::class)
class PersistenciaCrudTest {
    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private val nombre = "tutor_test_${System.nanoTime()}.db"
    private lateinit var helper: TutorDbHelper
    private lateinit var datos: HitoLocalDataSource

    @Before fun preparar() {
        helper = TutorDbHelper(contexto, nombre)
        datos = HitoLocalDataSource(helper)
    }

    @After fun limpiar() {
        helper.close()
        contexto.deleteDatabase(nombre)
    }

    @Test fun crearLeerActualizarEliminarAvanceYReabrirBase() {
        val hito = datos.obtenerHitos().first()
        val nuevoId = datos.agregarAvance(Avance(
            hitoId = hito.id, titulo = "Ensayo CRUD", descripcion = "Creación local", fecha = "2026-11-12"
        ))
        assertTrue(nuevoId > 0)
        assertTrue(datos.obtenerAvancesPorHito(hito.id).any { it.id == nuevoId })

        assertTrue(datos.actualizarAvance(Avance(
            id = nuevoId, hitoId = hito.id,
            titulo = "Ensayo actualizado", descripcion = "Edición local", fecha = "2026-11-12"
        )))
        helper.close()
        helper = TutorDbHelper(contexto, nombre)
        datos = HitoLocalDataSource(helper)
        assertEquals("Ensayo actualizado", datos.obtenerAvancesPorHito(hito.id)
            .first { it.id == nuevoId }.titulo)

        assertTrue(datos.eliminarAvance(nuevoId))
        assertFalse(datos.obtenerAvancesPorHito(hito.id).any { it.id == nuevoId })
    }

    @Test fun fechaLimiteSeGuardaYPermaneceAlReabrir() {
        val hito = datos.obtenerHitos().first()
        assertTrue(datos.actualizarFechaLimite(hito.id, "20/12/2026"))
        helper.close()
        helper = TutorDbHelper(contexto, nombre)
        datos = HitoLocalDataSource(helper)
        assertEquals("20/12/2026", datos.obtenerHitoPorId(hito.id)?.fechaLimite)
    }
}
