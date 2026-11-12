package cl.duoc.tutorvirtual.data.local

import android.content.ContentValues
import android.database.Cursor
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.data.model.Hito

class HitoLocalDataSource(private val dbHelper: TutorDbHelper) {

    fun obtenerHitos(): List<Hito> {
        val lista = mutableListOf<Hito>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            TutorDbHelper.TABLE_HITOS,
            null, null, null, null, null,
            "${TutorDbHelper.COL_HITO_ID} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Hito(
                        id = it.getLong(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_ID)),
                        titulo = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_TITULO)),
                        descripcion = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_DESCRIPCION)),
                        formatoRequerido = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_FORMATO)),
                        fechaLimite = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_FECHA_LIMITE)),
                        estado = EstadoHito.fromString(it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_ESTADO))),
                        porcentajeAvance = it.getInt(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_PORCENTAJE)),
                        comentariosDocente = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_COMENTARIOS))
                    )
                )
            }
        }
        return lista
    }

    fun obtenerHitoPorId(id: Long): Hito? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TutorDbHelper.TABLE_HITOS,
            null,
            "${TutorDbHelper.COL_HITO_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return Hito(
                    id = it.getLong(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_ID)),
                    titulo = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_TITULO)),
                    descripcion = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_DESCRIPCION)),
                    formatoRequerido = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_FORMATO)),
                    fechaLimite = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_FECHA_LIMITE)),
                    estado = EstadoHito.fromString(it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_ESTADO))),
                    porcentajeAvance = it.getInt(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_PORCENTAJE)),
                    comentariosDocente = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_HITO_COMENTARIOS))
                )
            }
        }
        return null
    }

    fun actualizarEstadoHito(id: Long, nuevoEstado: EstadoHito, nuevoPorcentaje: Int): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(TutorDbHelper.COL_HITO_ESTADO, nuevoEstado.name)
            put(TutorDbHelper.COL_HITO_PORCENTAJE, nuevoPorcentaje)
        }
        val filas = db.update(
            TutorDbHelper.TABLE_HITOS,
            cv,
            "${TutorDbHelper.COL_HITO_ID} = ?",
            arrayOf(id.toString())
        )
        return filas > 0
    }

    fun actualizarFechaLimite(id: Long, fecha: String): Boolean {
        val cv = ContentValues().apply { put(TutorDbHelper.COL_HITO_FECHA_LIMITE, fecha) }
        return dbHelper.writableDatabase.update(
            TutorDbHelper.TABLE_HITOS, cv,
            "${TutorDbHelper.COL_HITO_ID} = ?", arrayOf(id.toString())
        ) > 0
    }

    fun actualizarAvance(avance: Avance): Boolean {
        val cv = ContentValues().apply {
            put(TutorDbHelper.COL_AVANCE_TITULO, avance.titulo)
            put(TutorDbHelper.COL_AVANCE_DESCRIPCION, avance.descripcion)
        }
        return dbHelper.writableDatabase.update(
            TutorDbHelper.TABLE_AVANCES, cv,
            "${TutorDbHelper.COL_AVANCE_ID} = ?", arrayOf(avance.id.toString())
        ) > 0
    }

    fun agregarAvance(avance: Avance): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(TutorDbHelper.COL_AVANCE_HITO_ID, avance.hitoId)
            put(TutorDbHelper.COL_AVANCE_TITULO, avance.titulo)
            put(TutorDbHelper.COL_AVANCE_DESCRIPCION, avance.descripcion)
            put(TutorDbHelper.COL_AVANCE_FECHA, avance.fecha)
            put(TutorDbHelper.COL_AVANCE_HORAS, avance.horasDedicadas)
            put(TutorDbHelper.COL_AVANCE_ADJUNTO, avance.rutaAdjunto)
        }
        return db.insert(TutorDbHelper.TABLE_AVANCES, null, cv)
    }

    fun obtenerAvancesPorHito(hitoId: Long): List<Avance> {
        val lista = mutableListOf<Avance>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TutorDbHelper.TABLE_AVANCES,
            null,
            "${TutorDbHelper.COL_AVANCE_HITO_ID} = ?",
            arrayOf(hitoId.toString()),
            null, null,
            "${TutorDbHelper.COL_AVANCE_ID} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Avance(
                        id = it.getLong(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_ID)),
                        hitoId = it.getLong(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_HITO_ID)),
                        titulo = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_TITULO)),
                        descripcion = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_DESCRIPCION)),
                        fecha = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_FECHA)),
                        horasDedicadas = it.getInt(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_HORAS)),
                        rutaAdjunto = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_ADJUNTO))
                    )
                )
            }
        }
        return lista
    }

    fun obtenerTodosLosAvances(): List<Avance> {
        val lista = mutableListOf<Avance>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TutorDbHelper.TABLE_AVANCES,
            null, null, null, null, null,
            "${TutorDbHelper.COL_AVANCE_ID} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Avance(
                        id = it.getLong(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_ID)),
                        hitoId = it.getLong(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_HITO_ID)),
                        titulo = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_TITULO)),
                        descripcion = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_DESCRIPCION)),
                        fecha = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_FECHA)),
                        horasDedicadas = it.getInt(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_HORAS)),
                        rutaAdjunto = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_AVANCE_ADJUNTO))
                    )
                )
            }
        }
        return lista
    }

    fun eliminarAvance(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val filas = db.delete(
            TutorDbHelper.TABLE_AVANCES,
            "${TutorDbHelper.COL_AVANCE_ID} = ?",
            arrayOf(id.toString())
        )
        return filas > 0
    }
}
