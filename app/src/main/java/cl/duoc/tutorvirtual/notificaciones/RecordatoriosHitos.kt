package cl.duoc.tutorvirtual.notificaciones

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import cl.duoc.tutorvirtual.MainActivity
import cl.duoc.tutorvirtual.data.local.HitoLocalDataSource
import cl.duoc.tutorvirtual.data.local.TutorDbHelper
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.data.model.Hito
import cl.duoc.tutorvirtual.util.FechasHito
import java.time.ZoneId

/** Alarmas y notificaciones del dispositivo; no requiere Internet ni Firebase. */
object RecordatoriosHitos {
    private const val CANAL_ID = "recordatorios_hitos"
    private const val ACCION_RECORDAR = "cl.duoc.tutorvirtual.RECORDAR_HITO"
    private const val EXTRA_ID = "hito_id"
    private const val EXTRA_TITULO = "hito_titulo"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val canal = NotificationChannel(CANAL_ID, "Recordatorios de hitos", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Avisos locales antes del vencimiento de hitos"
            }
            manager.createNotificationChannel(canal)
        }
    }

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun intentAlarma(context: Context, hito: Hito): PendingIntent {
        val intent = Intent(context, ReceptorRecordatorio::class.java).apply {
            action = ACCION_RECORDAR
            putExtra(EXTRA_ID, hito.id)
            putExtra(EXTRA_TITULO, hito.titulo)
        }
        return PendingIntent.getBroadcast(
            context, hito.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancelar(context: Context, hito: Hito) {
        val alarmas = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmas.cancel(intentAlarma(context, hito))
    }

    fun programar(context: Context, hito: Hito) {
        cancelar(context, hito)
        if (hito.estado == EstadoHito.APROBADO || hito.estado == EstadoHito.ENVIADO) return
        val fecha = FechasHito.leer(hito.fechaLimite) ?: return
        // 09:00 del día anterior, en la zona horaria local del dispositivo.
        val instante = fecha.minusDays(1).atTime(9, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (instante <= System.currentTimeMillis()) return
        val alarmas = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Es una alarma inexacta: no necesita permiso de alarmas exactas.
        alarmas.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, instante, intentAlarma(context, hito))
    }

    fun programarTodos(context: Context, hitos: List<Hito>) {
        crearCanal(context)
        hitos.forEach { programar(context, it) }
    }

    fun mostrar(context: Context, titulo: String, cuerpo: String, idNotificacion: Int) {
        if (!tienePermiso(context)) return
        crearCanal(context)
        val abrir = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificacion = Notification.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setContentIntent(abrir)
            .setAutoCancel(true)
            .build()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(idNotificacion, notificacion)
    }

    fun mostrarPrueba(context: Context) {
        mostrar(context, "Tutor Virtual ESUCAR", "Notificación local de prueba: recordatorios activos.", 9000)
    }

    internal fun mostrarHito(context: Context, id: Long, titulo: String) {
        // Revalidar el estado por si se completó antes de que llegara el recordatorio.
        val hito = HitoLocalDataSource(TutorDbHelper(context)).obtenerHitoPorId(id)
        if (hito != null && hito.estado != EstadoHito.ENVIADO && hito.estado != EstadoHito.APROBADO) {
            mostrar(context, "Entrega próxima: $titulo", "Tu hito vence mañana. Revisa tus avances.", id.toInt())
        }
    }

    internal fun actualizarTrasReinicio(context: Context) {
        val hitos = HitoLocalDataSource(TutorDbHelper(context)).obtenerHitos()
        programarTodos(context, hitos)
    }

    internal fun esRecordatorio(action: String?) = action == ACCION_RECORDAR
    internal fun leerId(intent: Intent) = intent.getLongExtra(EXTRA_ID, -1L)
    internal fun leerTitulo(intent: Intent) = intent.getStringExtra(EXTRA_TITULO).orEmpty()
}

class ReceptorRecordatorio : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (RecordatoriosHitos.esRecordatorio(intent.action)) {
            RecordatoriosHitos.mostrarHito(context, RecordatoriosHitos.leerId(intent), RecordatoriosHitos.leerTitulo(intent))
        }
    }
}

class ReceptorReinicio : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            RecordatoriosHitos.actualizarTrasReinicio(context)
        }
    }
}
