package cl.duoc.tutorvirtual.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TutorDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "tutor_esucar.db"
        const val DATABASE_VERSION = 1

        // Tabla Hitos
        const val TABLE_HITOS = "hitos"
        const val COL_HITO_ID = "id"
        const val COL_HITO_TITULO = "titulo"
        const val COL_HITO_DESCRIPCION = "descripcion"
        const val COL_HITO_FORMATO = "formato"
        const val COL_HITO_FECHA_LIMITE = "fecha_limite"
        const val COL_HITO_ESTADO = "estado"
        const val COL_HITO_PORCENTAJE = "porcentaje"
        const val COL_HITO_COMENTARIOS = "comentarios"

        // Tabla Avances
        const val TABLE_AVANCES = "avances"
        const val COL_AVANCE_ID = "id"
        const val COL_AVANCE_HITO_ID = "hito_id"
        const val COL_AVANCE_TITULO = "titulo"
        const val COL_AVANCE_DESCRIPCION = "descripcion"
        const val COL_AVANCE_FECHA = "fecha"
        const val COL_AVANCE_HORAS = "horas"
        const val COL_AVANCE_ADJUNTO = "ruta_adjunto"

        // Tabla FAQs
        const val TABLE_FAQS = "faqs"
        const val COL_FAQ_ID = "id"
        const val COL_FAQ_CATEGORIA = "categoria"
        const val COL_FAQ_PREGUNTA = "pregunta"
        const val COL_FAQ_RESPUESTA = "respuesta"

        // Tabla Perfil
        const val TABLE_PERFIL = "perfil"
        const val COL_PERFIL_ID = "id"
        const val COL_PERFIL_NOMBRE = "nombre"
        const val COL_PERFIL_RUT = "rut"
        const val COL_PERFIL_GRADO = "grado"
        const val COL_PERFIL_EMAIL = "email"
        const val COL_PERFIL_ASIGNATURA = "asignatura"
        const val COL_PERFIL_TEMA = "tema_tesis"
        const val COL_PERFIL_GUIA = "profesor_guia"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createHitosTable = """
            CREATE TABLE $TABLE_HITOS (
                $COL_HITO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_HITO_TITULO TEXT NOT NULL,
                $COL_HITO_DESCRIPCION TEXT NOT NULL,
                $COL_HITO_FORMATO TEXT NOT NULL,
                $COL_HITO_FECHA_LIMITE TEXT NOT NULL,
                $COL_HITO_ESTADO TEXT NOT NULL,
                $COL_HITO_PORCENTAJE INTEGER NOT NULL DEFAULT 0,
                $COL_HITO_COMENTARIOS TEXT NOT NULL DEFAULT ''
            )
        """.trimIndent()

        val createAvancesTable = """
            CREATE TABLE $TABLE_AVANCES (
                $COL_AVANCE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_AVANCE_HITO_ID INTEGER NOT NULL,
                $COL_AVANCE_TITULO TEXT NOT NULL,
                $COL_AVANCE_DESCRIPCION TEXT NOT NULL,
                $COL_AVANCE_FECHA TEXT NOT NULL,
                $COL_AVANCE_HORAS INTEGER NOT NULL DEFAULT 1,
                $COL_AVANCE_ADJUNTO TEXT,
                FOREIGN KEY ($COL_AVANCE_HITO_ID) REFERENCES $TABLE_HITOS($COL_HITO_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        val createFaqsTable = """
            CREATE TABLE $TABLE_FAQS (
                $COL_FAQ_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FAQ_CATEGORIA TEXT NOT NULL,
                $COL_FAQ_PREGUNTA TEXT NOT NULL,
                $COL_FAQ_RESPUESTA TEXT NOT NULL
            )
        """.trimIndent()

        val createPerfilTable = """
            CREATE TABLE $TABLE_PERFIL (
                $COL_PERFIL_ID INTEGER PRIMARY KEY,
                $COL_PERFIL_NOMBRE TEXT NOT NULL,
                $COL_PERFIL_RUT TEXT NOT NULL,
                $COL_PERFIL_GRADO TEXT NOT NULL,
                $COL_PERFIL_EMAIL TEXT NOT NULL,
                $COL_PERFIL_ASIGNATURA TEXT NOT NULL,
                $COL_PERFIL_TEMA TEXT NOT NULL,
                $COL_PERFIL_GUIA TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createHitosTable)
        db.execSQL(createAvancesTable)
        db.execSQL(createFaqsTable)
        db.execSQL(createPerfilTable)

        poblarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AVANCES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HITOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAQS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERFIL")
        onCreate(db)
    }

    private fun poblarDatosIniciales(db: SQLiteDatabase) {
        // 1. Hitos iniciales para Trabajo de Título ESUCAR
        val hitosIniciales = listOf(
            Triple(
                "Hito 1: Planteamiento del Problema",
                "Definir el problema de investigación en seguridad pública, pregunta central y objetivos general y específicos.",
                "15/11/2026"
            ),
            Triple(
                "Hito 2: Marco Teórico y Legal",
                "Compilación de antecedentes institucionales, doctrinas policiales, base legal chilena y normas APA 7.",
                "30/11/2026"
            ),
            Triple(
                "Hito 3: Diseño Metodológico",
                "Definición de enfoque (cualitativo/cuantitativo), población, instrumentos de recolección y consentimiento informado.",
                "15/12/2026"
            ),
            Triple(
                "Hito 4: Recolección y Análisis de Datos",
                "Aplicación de encuestas/entrevistas, procesamiento de datos y contraste con los objetivos planteados.",
                "05/01/2027"
            ),
            Triple(
                "Hito 5: Informe Final y Propuesta de Mejora",
                "Consolidación del informe final de título, propuesta institucional aplicable a ESUCAR y presentación de defensa.",
                "20/01/2027"
            )
        )

        hitosIniciales.forEachIndexed { index, (titulo, desc, fecha) ->
            val cv = ContentValues().apply {
                put(COL_HITO_TITULO, titulo)
                put(COL_HITO_DESCRIPCION, desc)
                put(COL_HITO_FORMATO, "Documento Word / PDF según norma institucional ESUCAR y APA 7")
                put(COL_HITO_FECHA_LIMITE, fecha)
                put(COL_HITO_ESTADO, if (index == 0) "EN_DESARROLLO" else "POR_INICIAR")
                put(COL_HITO_PORCENTAJE, if (index == 0) 40 else 0)
                put(COL_HITO_COMENTARIOS, if (index == 0) "Revisar delimitación territorial en la comuna asignada." else "")
            }
            val idHito = db.insert(TABLE_HITOS, null, cv)

            // Si es el primer hito, agregamos un avance inicial de ejemplo
            if (index == 0) {
                val cvAvance = ContentValues().apply {
                    put(COL_AVANCE_HITO_ID, idHito)
                    put(COL_AVANCE_TITULO, "Redacción preliminar de objetivos")
                    put(COL_AVANCE_DESCRIPCION, "Se redactó el objetivo general y 3 objetivos específicos alineados a la pauta.")
                    put(COL_AVANCE_FECHA, "28/10/2026")
                    put(COL_AVANCE_HORAS, 4)
                    put(COL_AVANCE_ADJUNTO, null as String?)
                }
                db.insert(TABLE_AVANCES, null, cvAvance)
            }
        }

        // 2. FAQs iniciales
        val faqsIniciales = listOf(
            Triple(
                "Norma APA",
                "¿Cómo citar una ley o decreto institucional en APA 7ma edición?",
                "Se debe indicar el organismo emisor (ej: Carabineros de Chile o Ministerio del Interior), el año entre paréntesis, el título de la ordenanza o ley en cursiva y la fuente oficial (Diario Oficial)."
            ),
            Triple(
                "Metodología",
                "¿Qué diferencia existe entre un enfoque cualitativo y cuantitativo?",
                "El cuantitativo mide variables numéricas y estadísticas (ej: tasas delictuales o tiempos de respuesta), mientras que el cualitativo comprende percepciones y experiencias mediante entrevistas o grupos focales."
            ),
            Triple(
                "Formato",
                "¿Cuáles son los márgenes y tipografía oficial del informe?",
                "Márgenes de 2.54 cm en los cuatro costados, texto justificado o alineado a la izquierda según pauta ESUCAR, interlineado 1.5, tipografía Arial o Times New Roman de 12 puntos."
            ),
            Triple(
                "Entregas",
                "¿Qué ocurre si requiero una prórroga para la entrega de un hito?",
                "Debes comunicarte previamente con tu profesor guía antes de la fecha límite estipulada y justificar debidamente por correo institucional o por el conducto regular."
            ),
            Triple(
                "Metodología",
                "¿Cómo formular la pregunta de investigación de forma correcta?",
                "Debe ser precisa, no responderse con un simple 'sí' o 'no', delimitar temporal y espacialmente el problema y guardar relación directa con el objetivo general."
            )
        )

        faqsIniciales.forEach { (cat, preg, resp) ->
            val cvFaq = ContentValues().apply {
                put(COL_FAQ_CATEGORIA, cat)
                put(COL_FAQ_PREGUNTA, preg)
                put(COL_FAQ_RESPUESTA, resp)
            }
            db.insert(TABLE_FAQS, null, cvFaq)
        }

        // 3. Perfil inicial del estudiante (Cabos / Sargentos ESUCAR)
        val cvPerfil = ContentValues().apply {
            put(COL_PERFIL_ID, 1)
            put(COL_PERFIL_NOMBRE, "Sargento 2° Rodrigo Morales")
            put(COL_PERFIL_RUT, "18.345.678-9")
            put(COL_PERFIL_GRADO, "Sargento 2°")
            put(COL_PERFIL_EMAIL, "rodrigo.morales@esucar.cl")
            put(COL_PERFIL_ASIGNATURA, "Trabajo de Título II")
            put(COL_PERFIL_TEMA, "Optimización del patrullaje preventivo mediante análisis georreferenciado")
            put(COL_PERFIL_GUIA, "Prof. Nelson Melo")
        }
        db.insert(TABLE_PERFIL, null, cvPerfil)
    }
}
