package cl.duoc.tutorvirtual.data.local

import android.content.ContentValues
import cl.duoc.tutorvirtual.data.model.PerfilEstudiante

class PerfilLocalDataSource(private val dbHelper: TutorDbHelper) {

    fun obtenerPerfil(): PerfilEstudiante {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TutorDbHelper.TABLE_PERFIL,
            null,
            "${TutorDbHelper.COL_PERFIL_ID} = 1",
            null, null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return PerfilEstudiante(
                    nombre = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_PERFIL_NOMBRE)),
                    rut = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_PERFIL_RUT)),
                    grado = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_PERFIL_GRADO)),
                    email = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_PERFIL_EMAIL)),
                    asignatura = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_PERFIL_ASIGNATURA)),
                    temaTesis = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_PERFIL_TEMA)),
                    profesorGuia = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_PERFIL_GUIA))
                )
            }
        }
        return PerfilEstudiante()
    }

    fun guardarPerfil(perfil: PerfilEstudiante): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(TutorDbHelper.COL_PERFIL_ID, 1)
            put(TutorDbHelper.COL_PERFIL_NOMBRE, perfil.nombre)
            put(TutorDbHelper.COL_PERFIL_RUT, perfil.rut)
            put(TutorDbHelper.COL_PERFIL_GRADO, perfil.grado)
            put(TutorDbHelper.COL_PERFIL_EMAIL, perfil.email)
            put(TutorDbHelper.COL_PERFIL_ASIGNATURA, perfil.asignatura)
            put(TutorDbHelper.COL_PERFIL_TEMA, perfil.temaTesis)
            put(TutorDbHelper.COL_PERFIL_GUIA, perfil.profesorGuia)
        }
        val filas = db.insertWithOnConflict(
            TutorDbHelper.TABLE_PERFIL,
            null,
            cv,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
        return filas > 0
    }
}
