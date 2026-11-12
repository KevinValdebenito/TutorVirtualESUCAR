# Tutor Virtual ESUCAR — versión local

Aplicación Android para acompañar el Trabajo de Título I, II y III. Está desarrollada en **Kotlin + Jetpack Compose** y almacena el perfil, los hitos, las preguntas frecuentes y los avances en **SQLite**, en el propio dispositivo.

## Funcionalidades

- Inicio y guía metodológica.
- Preguntas frecuentes con búsqueda y respuestas desplegables con `AnimatedVisibility`.
- Hitos y avances: **crear, consultar, editar y eliminar** avances de manera local.
- Cambiar estado y fecha límite de cada hito sin perder los registros existentes.
- `Snackbar` de confirmación tras guardar/editar/eliminar y `LinearProgressIndicator` cuando hay operaciones locales en curso.
- Perfil del estudiante con guardado SQLite y aviso de confirmación.
- Recordatorio local de hitos pendientes, programado para las **09:00 del día anterior** mediante `AlarmManager.setAndAllowWhileIdle`. Es **inexacto**: Android puede retrasarlo.
- Botón **Probar notificación local** en *Mis avances* para demostrar el permiso y el aviso sin esperar hasta la fecha real.

**No necesita Firebase, API remota ni Internet para funcionar.** El proyecto Gradle sí puede necesitar Internet la primera vez para descargar dependencias del entorno de desarrollo.

## Abrir y ejecutar

1. Abrir la carpeta que contiene `settings.gradle.kts` con Android Studio.
2. Dejar seleccionado **Gradle Wrapper** y sincronizar (`File > Sync Project with Gradle Files`).
3. Instalar el Android SDK que indica `app/build.gradle.kts` (API 37) si Android Studio lo solicita.
4. `Build > Make Project` y después ejecutar `app` sobre el emulador **Medium Phone API 35** u otro dispositivo con Android 8.0 o superior.
5. Ir a *Mis avances* y tocar **Probar notificación local**. En Android 13 o superior permitir las notificaciones cuando se solicite.
6. Para probar cambios de fecha límite, seleccionar **Cambiar fecha límite** dentro de un hito. La fecha se guarda en SQLite y el aviso se reprograma.

## Persistencia local

La base de datos se llama `tutor_esucar.db`. La app no borra los registros al cerrarse ni al reiniciar el emulador. **Desinstalar la aplicación o borrar sus datos sí elimina la información local.**

Las fechas se guardan como `dd/MM/yyyy`. Los hitos de ejemplo tienen fechas iniciales que pueden ser anteriores al día de la demostración; es posible editarlas desde la interfaz. Una alarma no se programa si la fecha/hora de aviso ya pasó.

Las notificaciones de hitos `ENVIADO` o `APROBADO` se cancelan y no se muestran. Los avisos se vuelven a programar tras reiniciar el dispositivo o actualizar la aplicación. Para pruebas inmediatas, usar el botón de prueba.

## Arquitectura / archivos principales

- `app/src/main/java/cl/duoc/tutorvirtual/MainActivity.kt`: navegación Compose.
- `data/local/TutorDbHelper.kt`: esquema SQLite, datos iniciales.
- `data/local/HitoLocalDataSource.kt`: operaciones de la tabla de hitos y avances.
- `data/repository/HitoRepository.kt`: repositorio y `StateFlow`.
- `ui/viewmodel/HitoViewModel.kt`: validación, estados de carga y eventos de mensajes.
- `ui/screens/AvancesScreen.kt`: avances CRUD, fechas y aviso de prueba.
- `ui/screens/FaqScreen.kt`: búsqueda y animación de respuestas.
- `ui/screens/PerfilScreen.kt`: perfil y confirmación de guardado.
- `notificaciones/RecordatoriosHitos.kt`: canales, permisos, alarmas y receptores.
- `util/FechasHito.kt`: validación/formato de fechas.

## Pruebas

```bash
# Desde la carpeta raíz del proyecto, con Android SDK configurado:
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```

En Windows, sustituir `./gradlew` por `gradlew.bat`.

Los tests instrumentados (`PersistenciaCrudTest.kt`) crean una **base de datos aislada** y no tocan la base de datos de uso normal. Los tests unitarios validan las fechas. También es necesario ejecutar las pruebas manuales en móvil y tablet indicadas en [docs/PLAN_PRUEBAS.md](docs/PLAN_PRUEBAS.md).

## Evidencias de evaluación

Consulta [docs/EVIDENCIAS_ENTREGA.md](docs/EVIDENCIAS_ENTREGA.md). Las capturas del emulador y Trello deben ser **reales**, obtenidas cuando se pruebe la app: esta entrega de código no inventa resultados de pruebas.
