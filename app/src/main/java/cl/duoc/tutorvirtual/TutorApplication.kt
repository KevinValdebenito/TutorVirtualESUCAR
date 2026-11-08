package cl.duoc.tutorvirtual

import android.app.Application
import cl.duoc.tutorvirtual.di.AppContainer
import cl.duoc.tutorvirtual.di.DefaultAppContainer

class TutorApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
