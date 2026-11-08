package cl.duoc.tutorvirtual.data.local

import android.database.Cursor
import cl.duoc.tutorvirtual.data.model.PreguntaFrecuente

class FaqLocalDataSource(private val dbHelper: TutorDbHelper) {

    fun obtenerFaqs(categoria: String? = null, query: String? = null): List<PreguntaFrecuente> {
        val lista = mutableListOf<PreguntaFrecuente>()
        val db = dbHelper.readableDatabase

        val condiciones = mutableListOf<String>()
        val args = mutableListOf<String>()

        if (!categoria.isNullOrBlank() && categoria != "Todas") {
            condiciones.add("${TutorDbHelper.COL_FAQ_CATEGORIA} = ?")
            args.add(categoria)
        }

        if (!query.isNullOrBlank()) {
            condiciones.add("(${TutorDbHelper.COL_FAQ_PREGUNTA} LIKE ? OR ${TutorDbHelper.COL_FAQ_RESPUESTA} LIKE ?)")
            args.add("%$query%")
            args.add("%$query%")
        }

        val selection = if (condiciones.isNotEmpty()) condiciones.joinToString(" AND ") else null
        val selectionArgs = if (args.isNotEmpty()) args.toTypedArray() else null

        val cursor: Cursor = db.query(
            TutorDbHelper.TABLE_FAQS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "${TutorDbHelper.COL_FAQ_ID} ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    PreguntaFrecuente(
                        id = it.getLong(it.getColumnIndexOrThrow(TutorDbHelper.COL_FAQ_ID)),
                        categoria = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_FAQ_CATEGORIA)),
                        pregunta = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_FAQ_PREGUNTA)),
                        respuesta = it.getString(it.getColumnIndexOrThrow(TutorDbHelper.COL_FAQ_RESPUESTA))
                    )
                )
            }
        }
        return lista
    }

    fun obtenerCategorias(): List<String> {
        val lista = mutableListOf("Todas")
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT ${TutorDbHelper.COL_FAQ_CATEGORIA} FROM ${TutorDbHelper.TABLE_FAQS}", null)
        cursor.use {
            while (it.moveToNext()) {
                lista.add(it.getString(0))
            }
        }
        return lista
    }
}
