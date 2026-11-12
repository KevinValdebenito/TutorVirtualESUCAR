# Plan de pruebas — Tutor Virtual ESUCAR

Marca cada prueba solo después de ejecutarla en Android Studio. En el informe, registra versión de Android, dispositivo, fecha, resultado y evidencia.

| ID | Prueba / acción | Resultado esperado | Estado | Evidencia |
|---|---|---|---|---|
| P01 | Crear un avance con título y descripción | Registro visible en su hito | Pendiente | Captura |
| P02 | Cerrar app, volver a abrirla | Avance se mantiene (SQLite) | Pendiente | Captura |
| P03 | Editar un avance | Nuevo texto aparece y persiste | Pendiente | Captura |
| P04 | Eliminar un avance y confirmar | Registro deja de mostrarse | Pendiente | Captura |
| P05 | Intentar crear con campos en blanco | Mensaje de validación | Pendiente | Captura |
| P06 | Guardar perfil válido | Confirmación y datos persistentes | Pendiente | Captura |
| P07 | Buscar FAQ y desplegar respuesta | `AnimatedVisibility` abre/cierra | Pendiente | Video o capturas |
| P08 | Registrar avance | Indicador durante la operación; `Snackbar` al finalizar | Pendiente | Video o capturas |
| P09 | Pulsar 'Probar notificación local' y autorizar permiso | Notificación visible en Android API 35 | Pendiente | Captura |
| P10 | Modificar la fecha de un hito y reabrir app | Nueva fecha persistente | Pendiente | Captura |
| P11 | Programar fecha para más de un día en el futuro | Alarma creada; recordatorio pendiente del horario real | Pendiente | Nota de verificación |
| P12 | Marcar hito como enviado | Aviso pendiente cancelado | Pendiente | Captura |
| P13 | Tablet virtual (ej. Pixel Tablet) | Contenido legible, sin cortes ni superposiciones | Pendiente | Captura horizontal y vertical |
| P14 | Cambiar orientación de pantalla | Contenido visible y usable | Pendiente | Captura |
| P15 | Ejecutar `testDebugUnitTest` | Tests unitarios pasan | Pendiente | Build Output |
| P16 | Ejecutar `connectedDebugAndroidTest` | Tests de SQLite pasan | Pendiente | Build Output |

## Emuladores sugeridos

1. **Medium Phone API 35** (actual).
2. **Pixel Tablet API 35**, desde `Tools > Device Manager > Create Virtual Device`.

## Observaciones

- Los mensajes de carga pueden ser muy breves porque SQLite local responde rápido: no añadimos retardos artificiales.
- La alarma local es inexacta y puede retrasarse según la batería/estado del emulador. El botón de prueba entrega evidencia inmediata de notificaciones, **no** demuestra por sí solo que el disparo programado sea puntual.
- En un dispositivo real, comprobar también comportamiento tras reinicio.
- No desinstalar la aplicación durante la prueba de persistencia, pues borraría los datos locales.
