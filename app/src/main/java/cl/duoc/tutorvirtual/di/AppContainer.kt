package cl.duoc.tutorvirtual.di

import android.content.Context
import cl.duoc.tutorvirtual.data.local.FaqLocalDataSource
import cl.duoc.tutorvirtual.data.local.HitoLocalDataSource
import cl.duoc.tutorvirtual.data.local.PerfilLocalDataSource
import cl.duoc.tutorvirtual.data.local.TutorDbHelper
import cl.duoc.tutorvirtual.data.repository.FaqRepository
import cl.duoc.tutorvirtual.data.repository.FaqRepositoryImpl
import cl.duoc.tutorvirtual.data.repository.HitoRepository
import cl.duoc.tutorvirtual.data.repository.HitoRepositoryImpl
import cl.duoc.tutorvirtual.data.repository.PerfilRepository
import cl.duoc.tutorvirtual.data.repository.PerfilRepositoryImpl

interface AppContainer {
    val hitoRepository: HitoRepository
    val faqRepository: FaqRepository
    val perfilRepository: PerfilRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val dbHelper: TutorDbHelper by lazy {
        TutorDbHelper(context)
    }

    private val hitoLocalDataSource: HitoLocalDataSource by lazy {
        HitoLocalDataSource(dbHelper)
    }

    private val faqLocalDataSource: FaqLocalDataSource by lazy {
        FaqLocalDataSource(dbHelper)
    }

    private val perfilLocalDataSource: PerfilLocalDataSource by lazy {
        PerfilLocalDataSource(dbHelper)
    }

    override val hitoRepository: HitoRepository by lazy {
        HitoRepositoryImpl(hitoLocalDataSource)
    }

    override val faqRepository: FaqRepository by lazy {
        FaqRepositoryImpl(faqLocalDataSource)
    }

    override val perfilRepository: PerfilRepository by lazy {
        PerfilRepositoryImpl(perfilLocalDataSource)
    }
}
