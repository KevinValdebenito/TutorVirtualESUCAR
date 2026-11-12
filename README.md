# Tutor Virtual ESUCAR 📚👮‍♂️
**Aplicación Móvil de Acompañamiento Académico para la Escuela de Suboficiales de Carabineros**

Proyecto desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)** — **Evaluación Parcial 2 (Encargo)**.

---

## 👥 Integrantes del Equipo
* **Kevin Valdebenito** ([@KevinValdebenito](https://github.com/KevinValdebenito))
* **Benjamín Sepúlveda** ([@benjasepulvedar-prog](https://github.com/benjasepulvedar-prog))

**Carrera:** Ingeniería en Informática / Analista Programador  
**Institución:** Duoc UC  
**Semestre:** 4to Semestre  

---

## 📖 Descripción del Proyecto
El **Tutor Virtual ESUCAR** es una solución móvil nativa en **Android** construida con **Jetpack Compose** y **Material 3**, diseñada para acompañar a los funcionarios alumnos de la Escuela de Suboficiales de Carabineros en el desarrollo de sus asignaturas de **Trabajo de Título I, II y III**.

La aplicación funciona de manera **100% offline**, garantizando disponibilidad continua para la consulta de guías metodológicas, preguntas frecuentes (FAQ), registro y seguimiento de hitos/avances de tesis, y gestión del perfil del estudiante con fotografía de credencial.

---

## 🎯 Cumplimiento de Indicadores de Evaluación (Rúbrica)

| Indicador | Ponderación | Aspecto Evaluado | Implementación en el Proyecto |
| :--- | :---: | :--- | :--- |
| **IE 2.1.1** | 15% | Jerarquía visual, usabilidad y navegación declarativa | Estructura con `Scaffold`, `NavigationBar` con íconos oficiales M3 y `NavHost` (`navigation-compose:2.10.2`) navegando fluidamente entre Inicio, Guía, FAQ, Avances y Perfil. |
| **IE 2.1.2** | 15% | Validación visual de formularios y control de errores | Campos con `isError`, `trailingIcon` con íconos de advertencia (`Icons.Default.Warning`) y mensajes de asistencia en tiempo real (`supportingText`). |
| **IE 2.2.1** | 10% | Lógica de validación desacoplada en ViewModel | `PerfilViewModel` gestiona `PerfilUiState` reactivo con `StateFlow`, aplicando la validación desacoplada en `validarFormulario()` con la clase `PerfilErrores`. |
| **IE 2.2.2** | 10% | Estados reactivos, loaders y animaciones funcionales | `AnimatedVisibility` con efectos de desvanecimiento (`fadeIn`/`fadeOut`) y expansión vertical en formularios, acordeón de FAQ interactivo y retroalimentación de guardado. Indicadores `CircularProgressIndicator` para estados de carga. |
| **IE 2.3.1** | 15% | Arquitectura MVVM y persistencia local sin conexión | Separación modular en capas (`data/local`, `data/model`, `data/repository`, `di`, `ui/screens`, `ui/viewmodel`). Base de datos local SQLite (`tutor_esucar.db`) y `SharedPreferences` para persistencia 100% offline. |
| **IE 2.4.1** | 15% | Integración de al menos dos recursos nativos | Acceso a **Cámara** (`ActivityResultContracts.TakePicture`) con `FileProvider` seguro y acceso a **Galería** (`ActivityResultContracts.GetContent`) para la foto de perfil. |
| **IE 2.3.2** | 20% | Trabajo colaborativo en Git y gestión de tareas | Flujo Git con ramas `main` y `develop`, Pull Requests revisados e integrados entre ambos integrantes, commits semánticos y seguimiento en tablero Trello. |

---

## 🏛️ Arquitectura del Software (MVVM)

El proyecto sigue estrictamente el estándar de arquitectura modular enseñado en las Guías 7 a 13 del curso:

```
cl.duoc.tutorvirtual/
├── MainActivity.kt                 # Punto de entrada, Scaffold y NavHost
├── TutorApplication.kt             # Application class que inicializa AppContainer
├── di/
│   └── AppContainer.kt             # Inyección de dependencias manual (repositorios y BD)
├── data/
│   ├── local/
│   │   ├── TutorDbHelper.kt        # SQLiteOpenHelper con DDL y datos semilla precargados
│   │   ├── HitoLocalDataSource.kt  # Operaciones CRUD para hitos y avances
│   │   ├── FaqLocalDataSource.kt   # Consultas de preguntas frecuentes por categoría/búsqueda
│   │   └── PerfilLocalDataSource.kt# Persistencia de datos del estudiante
│   ├── model/
│   │   ├── Hito.kt                 # Modelo de hitos y estados (EstadoHito)
│   │   ├── Avance.kt               # Modelo de avances de trabajo de título
│   │   ├── PreguntaFrecuente.kt    # Modelo de preguntas frecuentes
│   │   ├── PerfilEstudiante.kt     # Modelo de datos del perfil
│   │   └── PerfilUiState.kt        # Estado inmutable de UI y errores de validación
│   └── repository/
│       ├── HitoRepository.kt       # Repositorio de hitos con StateFlow reactivo
│       ├── FaqRepository.kt        # Repositorio de FAQs con StateFlow reactivo
│       ├── PerfilRepository.kt     # Repositorio del perfil del estudiante
│       └── GuiaRepository.kt       # Repositorio en memoria para etapas de la tesis
└── ui/
    ├── screens/
    │   ├── GuiaMetodologicaScreen.kt # Consulta de etapas con transiciones animadas
    │   ├── FaqScreen.kt              # Buscador y tarjetas acordeón animadas
    │   ├── AvancesScreen.kt          # Seguimiento de hitos y formulario animado
    │   ├── PerfilScreen.kt           # Formulario validado con resumen animado
    │   └── FotoPerfil.kt             # Integración nativa de Cámara y Galería
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    └── viewmodel/
        ├── HitoViewModel.kt          # ViewModel para hitos y avances
        ├── FaqViewModel.kt           # ViewModel para FAQs y filtros
        └── PerfilViewModel.kt        # ViewModel para perfil y validaciones desacopladas
```

---

## 📱 Funcionalidades Principales

### 1. Inicio y Navegación
* Menú de navegación inferior (`NavigationBar`) con íconos vectoriales Material 3.
* Enrutamiento declarativo seguro mediante `NavHost`.

### 2. Guía Metodológica
* Guía metodológica estructurada en cinco etapas: planteamiento del problema, marco teórico, metodología, recogida de información y análisis de resultados.
* Navegación interactiva entre etapas con animación fluida `AnimatedVisibility`.

### 3. Preguntas Frecuentes (FAQ)
* Búsqueda por texto y filtro por categoría (General, Formato, Fechas, Asesoría).
* Tarjetas tipo acordeón interactivo que expanden y contraen la respuesta con animación.

### 4. Seguimiento de Hitos y Registro de Avances
* Barra de progreso (`LinearProgressIndicator`) por cada entrega.
* Cambio de estados (`POR_INICIAR`, `EN_DESARROLLO`, `ENVIADO`, `APROBADO`).
* Formulario desplegable animado para registrar avances con validación de campos obligatorios.
* Diálogo de confirmación para eliminar avances.

### 5. Mi Perfil y Foto de Credencial
* Formulario con validación reactiva:
  * **Nombre:** Campo obligatorio.
  * **Correo:** Formato de correo electrónico válido.
  * **Clave de prueba:** Mínimo 6 caracteres; no se almacena.
  * **Dirección:** Campo obligatorio y persistido localmente.
  * **RUT, grado, asignatura y profesor guía:** Datos académicos cargados desde el perfil local.
* Indicadores visuales de error (`isError`, `trailingIcon` con advertencia y `supportingText`).
* Visualización animada de tarjeta resumen una vez guardado con éxito.
* **Recursos Nativos:**
  * **Cámara:** Permite capturar la foto usando la cámara del dispositivo con `FileProvider`.
  * **Galería:** Permite seleccionar una foto existente desde el almacenamiento del dispositivo.
  * Persistencia segura de la ruta en almacenamiento local (`context.filesDir`).

---

## 🛠️ Tecnologías y Requisitos Previos

* **Lenguaje:** Kotlin 2.2+
* **Framework UI:** Jetpack Compose con Material 3 (BOM 2026.02.01)
* **SDK Mínimo:** Android API 26 (Android 8.0 Oreo)
* **SDK Compilación / Objetivo:** Android API 37
* **Entorno de Desarrollo:** Android Studio Ladybug / Meerkat o superior
* **JDK:** Java 17 o superior
* **Herramienta de Construcción:** Gradle 9.6

---

## 🚀 Instrucciones de Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/KevinValdebenito/TutorVirtualESUCAR.git
   cd TutorVirtualESUCAR
   ```

2. **Cambiar a la rama de desarrollo:**
   ```bash
   git checkout develop
   ```

3. **Abrir en Android Studio:**
   * Abrir Android Studio y seleccionar **File > Open...**
   * Seleccionar la carpeta raíz del proyecto (`TutorVirtualESUCAR`).
   * Esperar a que Gradle sincronice las dependencias del proyecto.

4. **Compilar y Ejecutar:**
   * Conectar un dispositivo físico con depuración USB habilitada o iniciar un Emulador Android (API 26 o superior).
   * Presionar el botón **Run 'app'** (`Shift + F10`) en Android Studio.
   * Alternativamente, compilar desde la terminal:
     ```bash
     ./gradlew assembleDebug
     ```
