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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.notificaciones.RecordatoriosHitos
import cl.duoc.tutorvirtual.ui.components.ContenidoAdaptable
import cl.duoc.tutorvirtual.ui.viewmodel.HitoViewModel
import cl.duoc.tutorvirtual.util.FechasHito
import java.time.LocalDate

@Composable
fun AvancesScreen() {

    val modelo: HitoViewModel = viewModel()

    val hitos by modelo.hitos.collectAsState()
    val avances by modelo.avances.collectAsState()
    val cargando by modelo.cargando.collectAsState()
    val formulario by modelo.formularioAvance.collectAsState()
    val contexto = LocalContext.current
    var mostrarPruebaAlConcederPermiso by remember { mutableStateOf(false) }

    val permisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido && mostrarPruebaAlConcederPermiso) {
            RecordatoriosHitos.mostrarPrueba(contexto)
        }
        mostrarPruebaAlConcederPermiso = false
    }

    fun probarNotificacion() {
        if (RecordatoriosHitos.tienePermiso(contexto)) {
            RecordatoriosHitos.mostrarPrueba(contexto)
        } else if (Build.VERSION.SDK_INT >= 33) {
            mostrarPruebaAlConcederPermiso = true
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var avanceParaEliminar by remember {
        mutableStateOf<Avance?>(null)
    }

    if (avanceParaEliminar != null) {

        AlertDialog(
            onDismissRequest = {
                avanceParaEliminar = null
            },
            title = {
                Text("Eliminar avance")
            },
            text = {
                Text("¿Seguro que deseas eliminar este avance?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val avance = avanceParaEliminar

                        if (avance != null) {
                            modelo.eliminarAvance(avance.id)
                        }

                        avanceParaEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        avanceParaEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    ContenidoAdaptable {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    "Mis avances",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text("Seguimiento de tu trabajo de título")

                Button(
                    onClick = { probarNotificacion() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Probar notificación local")
                }
                Text(
                    "Los recordatorios se programan para las 09:00 del día anterior a cada hito pendiente.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (cargando) {

                item {
                    CircularProgressIndicator()
                }

            } else if (hitos.isEmpty()) {

                item {
                    Text("No hay hitos registrados.")
                }

            } else {

                items(hitos, key = { it.id }) { hito ->
                    var mostrarPauta by rememberSaveable(hito.id) { mutableStateOf(false) }
                    val mostrarFormulario = formulario.visible && formulario.hitoId == hito.id

                    val avancesDelHito = avances.filter {
                        it.hitoId == hito.id
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Text(
                                hito.titulo,
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Text(hito.descripcion)

                            Text("Fecha límite: ${hito.fechaLimite}")

                            OutlinedButton(
                                onClick = { mostrarPauta = !mostrarPauta },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (mostrarPauta) "Ocultar pauta del hito" else "Ver pauta del hito")
                            }

                            AnimatedVisibility(
                                visible = mostrarPauta,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            "Formato requerido",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(hito.formatoRequerido)

                                        Text(
                                            "Criterios de evaluación",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        hito.criteriosEvaluacion
                                            .lineSequence()
                                            .filter { it.isNotBlank() }
                                            .forEach { criterio -> Text("• $criterio") }

                                        if (hito.comentariosDocente.isNotBlank()) {
                                            Text(
                                                "Comentario del profesor guía",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(hito.comentariosDocente)
                                        }
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val fechaInicial =
                                        FechasHito.leer(hito.fechaLimite) ?: LocalDate.now()
                                    val selector = DatePickerDialog(
                                        contexto,
                                        { _, anio, mes, dia ->
                                            val fecha = FechasHito.mostrar(
                                                LocalDate.of(anio, mes + 1, dia)
                                            )
                                            modelo.actualizarFechaLimite(hito.id, fecha)
                                        },
                                        fechaInicial.year,
                                        fechaInicial.monthValue - 1,
                                        fechaInicial.dayOfMonth
                                    )
                                    selector.datePicker.minDate = System.currentTimeMillis()
                                    selector.show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cambiar fecha límite")
                            }

                            Text("Estado: ${hito.estado.label}")

                            Text("Progreso: ${hito.porcentajeAvance}%")

                            LinearProgressIndicator(
                                progress = {
                                    hito.porcentajeAvance / 100f
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (hito.estado == EstadoHito.POR_INICIAR) {

                                OutlinedButton(
                                    onClick = {
                                        modelo.cambiarEstado(
                                            hito.id,
                                            EstadoHito.EN_DESARROLLO,
                                            25
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Comenzar hito")
                                }
                            }

                            if (hito.estado != EstadoHito.ENVIADO &&
                                hito.estado != EstadoHito.APROBADO
                            ) {

                                Button(
                                    onClick = {
                                        modelo.cambiarEstado(
                                            hito.id,
                                            EstadoHito.ENVIADO,
                                            100
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Marcar como enviado")
                                }
                            }

                            HorizontalDivider()

                            Text(
                                "Avances registrados",
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            if (avancesDelHito.isEmpty()) {
                                Text("Aún no hay avances registrados.")
                            }

                            avancesDelHito.forEach { avance ->

                                Text(
                                    avance.titulo,
                                    style =
                                        MaterialTheme.typography.titleSmall
                                )

                                Text(avance.descripcion)

                                Text("Fecha: ${avance.fecha}")

                                TextButton(
                                    onClick = {
                                        avanceParaEliminar = avance
                                    }
                                ) {
                                    Text("Eliminar avance")
                                }

                                HorizontalDivider()
                            }

                            // MOSTRAR FORMULARIO
                            if (hito.estado != EstadoHito.APROBADO) {

                                OutlinedButton(
                                    onClick = {
                                        modelo.alternarFormularioAvance(hito.id)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        if (mostrarFormulario)
                                            "Cancelar"
                                        else
                                            "Registrar avance"
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = mostrarFormulario,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = formulario.titulo,
                                        onValueChange = modelo::onTituloAvanceChange,
                                        label = {
                                            Text("Título del avance")
                                        },
                                        isError = formulario.errorTitulo != null,
                                        trailingIcon = {
                                            if (formulario.errorTitulo != null) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = "Error",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        },
                                        supportingText = {
                                            formulario.errorTitulo?.let { Text(it) }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = formulario.descripcion,
                                        onValueChange = modelo::onDescripcionAvanceChange,
                                        label = {
                                            Text("Descripción")
                                        },
                                        isError = formulario.errorDescripcion != null,
                                        trailingIcon = {
                                            if (formulario.errorDescripcion != null) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = "Error",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        },
                                        supportingText = {
                                            formulario.errorDescripcion?.let { Text(it) }
                                        },
                                        minLines = 3,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    formulario.errorGeneral?.let { mensaje ->
                                        Text(
                                            mensaje,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            modelo.registrarAvance()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Guardar avance")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
