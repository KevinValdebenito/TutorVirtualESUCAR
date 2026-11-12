package cl.duoc.tutorvirtual.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.notificaciones.RecordatoriosHitos
import cl.duoc.tutorvirtual.ui.viewmodel.EventoAvances
import cl.duoc.tutorvirtual.ui.viewmodel.HitoViewModel
import cl.duoc.tutorvirtual.util.FechasHito
import java.time.LocalDate

@Composable
fun AvancesScreen() {
    val modelo: HitoViewModel = viewModel()
    val hitos by modelo.hitos.collectAsState()
    val avances by modelo.avances.collectAsState()
    val cargando by modelo.cargando.collectAsState()
    val operando by modelo.operando.collectAsState()
    val contexto = LocalContext.current
    val snackbar = remember { SnackbarHostState() }

    var formularioHitoId by remember { mutableStateOf<Long?>(null) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }
    var editar by remember { mutableStateOf<Avance?>(null) }
    var editarTitulo by remember { mutableStateOf("") }
    var editarDescripcion by remember { mutableStateOf("") }
    var eliminar by remember { mutableStateOf<Avance?>(null) }
    var hacerPruebaDespuesDePermiso by remember { mutableStateOf(false) }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido && hacerPruebaDespuesDePermiso) RecordatoriosHitos.mostrarPrueba(contexto)
        hacerPruebaDespuesDePermiso = false
    }

    LaunchedEffect(modelo) {
        modelo.eventos.collect { evento ->
            when (evento) {
                is EventoAvances.Mensaje -> snackbar.showSnackbar(evento.texto)
                EventoAvances.AvanceGuardado -> {
                    formularioHitoId = null
                    titulo = ""
                    descripcion = ""
                    mostrarError = false
                }
                EventoAvances.AvanceEditado -> editar = null
            }
        }
    }

    fun notificacionDePrueba() {
        if (RecordatoriosHitos.tienePermiso(contexto)) {
            RecordatoriosHitos.mostrarPrueba(contexto)
        } else if (Build.VERSION.SDK_INT >= 33) {
            hacerPruebaDespuesDePermiso = true
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (eliminar != null) {
        AlertDialog(
            onDismissRequest = { eliminar = null },
            title = { Text("Eliminar avance") },
            text = { Text("¿Seguro que quieres eliminar este avance?") },
            confirmButton = {
                TextButton(enabled = !operando, onClick = {
                    eliminar?.let { modelo.eliminarAvance(it.id) }
                    eliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { eliminar = null }) { Text("Cancelar") } }
        )
    }

    if (editar != null) {
        AlertDialog(
            onDismissRequest = { if (!operando) editar = null },
            title = { Text("Editar avance") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(editarTitulo, { editarTitulo = it }, label = { Text("Título") }, singleLine = true)
                    OutlinedTextField(editarDescripcion, { editarDescripcion = it }, label = { Text("Descripción") }, minLines = 2)
                    if (editarTitulo.isBlank() || editarDescripcion.isBlank()) {
                        Text("Ambos campos son obligatorios", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !operando && editarTitulo.isNotBlank() && editarDescripcion.isNotBlank(),
                    onClick = { editar?.let { modelo.actualizarAvance(it.copy(titulo = editarTitulo, descripcion = editarDescripcion)) } }
                ) { Text("Guardar cambios") }
            },
            dismissButton = { TextButton(enabled = !operando, onClick = { editar = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 880.dp).fillMaxWidth().fillMaxHeight(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Mis avances", style = MaterialTheme.typography.headlineSmall)
                    Text("Seguimiento local de tu trabajo de título")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { notificacionDePrueba() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Probar notificación local")
                    }
                    Text("Recordatorio: 09:00 del día anterior a cada hito pendiente. " +
                            "En Android 13+ debes autorizar las notificaciones.",
                        style = MaterialTheme.typography.bodySmall)
                    if (operando) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                if (cargando) item { CircularProgressIndicator() }
                else if (hitos.isEmpty()) item { Text("No hay hitos registrados.") }
                else items(hitos, key = { it.id }) { hito ->
                    val avancesHito = avances.filter { it.hitoId == hito.id }
                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(hito.titulo, style = MaterialTheme.typography.titleLarge)
                            Text(hito.descripcion)
                            Text("Fecha límite: ${hito.fechaLimite}")
                            OutlinedButton(enabled = !operando, onClick = {
                                val inicial = FechasHito.leer(hito.fechaLimite) ?: LocalDate.now()
                                DatePickerDialog(contexto, { _, anio, mes, dia ->
                                    modelo.actualizarFechaLimite(hito.id,
                                        FechasHito.mostrar(LocalDate.of(anio, mes + 1, dia)))
                                }, inicial.year, inicial.monthValue - 1, inicial.dayOfMonth).show()
                            }) { Text("Cambiar fecha límite") }
                            Text("Estado: ${hito.estado.label}")
                            Text("Progreso: ${hito.porcentajeAvance}%")
                            LinearProgressIndicator(
                                progress = { hito.porcentajeAvance.coerceIn(0, 100) / 100f },
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (hito.estado == EstadoHito.POR_INICIAR) {
                                OutlinedButton(
                                    modifier = Modifier.fillMaxWidth(), enabled = !operando,
                                    onClick = { modelo.cambiarEstado(hito.id, EstadoHito.EN_DESARROLLO, 25) }
                                ) { Text("Comenzar hito") }
                            }
                            if (hito.estado != EstadoHito.ENVIADO && hito.estado != EstadoHito.APROBADO) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(), enabled = !operando,
                                    onClick = { modelo.cambiarEstado(hito.id, EstadoHito.ENVIADO, 100) }
                                ) { Text("Marcar como enviado") }
                            }
                            HorizontalDivider()
                            Text("Avances registrados", style = MaterialTheme.typography.titleMedium)
                            if (avancesHito.isEmpty()) Text("Aún no hay avances registrados.")
                            avancesHito.forEach { avance ->
                                Text(avance.titulo, style = MaterialTheme.typography.titleSmall)
                                Text(avance.descripcion)
                                Text("Fecha: ${avance.fecha}")
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(enabled = !operando, onClick = {
                                        editar = avance
                                        editarTitulo = avance.titulo
                                        editarDescripcion = avance.descripcion
                                    }) { Text("Editar") }
                                    TextButton(enabled = !operando, onClick = { eliminar = avance }) {
                                        Text("Eliminar")
                                    }
                                }
                                HorizontalDivider()
                            }
                            if (hito.estado != EstadoHito.APROBADO) {
                                OutlinedButton(modifier = Modifier.fillMaxWidth(), enabled = !operando,
                                    onClick = {
                                        formularioHitoId = if (formularioHitoId == hito.id) null else hito.id
                                        titulo = ""
                                        descripcion = ""
                                        mostrarError = false
                                    }) {
                                    Text(if (formularioHitoId == hito.id) "Cerrar formulario" else "Registrar avance")
                                }
                            }
                            AnimatedVisibility(
                                visible = formularioHitoId == hito.id,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = titulo, onValueChange = { titulo = it },
                                        label = { Text("Título del avance") },
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = mostrarError && titulo.isBlank()
                                    )
                                    OutlinedTextField(
                                        value = descripcion, onValueChange = { descripcion = it },
                                        label = { Text("Descripción") }, minLines = 3,
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = mostrarError && descripcion.isBlank()
                                    )
                                    if (mostrarError) Text("Completa ambos campos", color = MaterialTheme.colorScheme.error)
                                    Button(modifier = Modifier.fillMaxWidth(), enabled = !operando,
                                        onClick = {
                                            mostrarError = titulo.isBlank() || descripcion.isBlank()
                                            if (!mostrarError) modelo.registrarAvance(hito.id, titulo, descripcion)
                                        }) { Text("Guardar avance") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
