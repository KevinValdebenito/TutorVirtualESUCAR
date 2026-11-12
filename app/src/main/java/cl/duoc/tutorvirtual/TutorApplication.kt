package cl.duoc.tutorvirtual

import android.app.Application
import cl.duoc.tutorvirtual.di.AppContainer
import cl.duoc.tutorvirtual.di.DefaultAppContainer
import cl.duoc.tutorvirtual.notificaciones.RecordatoriosHitos

class TutorApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        // Las fechas e hitos se leen de SQLite; todo funciona sin conexión.
        RecordatoriosHitos.programarTodos(this, container.hitoRepository.hitosFlow.value)
    }
}
