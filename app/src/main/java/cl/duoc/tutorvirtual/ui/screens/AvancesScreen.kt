
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.notificaciones.RecordatoriosHitos
import cl.duoc.tutorvirtual.ui.viewmodel.HitoViewModel
import cl.duoc.tutorvirtual.util.FechasHito
import java.time.LocalDate

@Composable
fun AvancesScreen() {

    val modelo: HitoViewModel = viewModel()

    val hitos by modelo.hitos.collectAsState()
    val avances by modelo.avances.collectAsState()
    val cargando by modelo.cargando.collectAsState()
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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

                var mostrarFormulario by remember {
                    mutableStateOf(false)
                }

                var titulo by remember {
                    mutableStateOf("")
                }

                var descripcion by remember {
                    mutableStateOf("")
                }

                var mostrarError by remember {
                    mutableStateOf(false)
                }

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
                            onClick = {
                                val fechaInicial = FechasHito.leer(hito.fechaLimite) ?: LocalDate.now()
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
                                    mostrarFormulario = !mostrarFormulario
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
                                    value = titulo,
                                    onValueChange = {
                                        titulo = it
                                    },
                                    label = {
                                        Text("Título del avance")
                                    },
                                    isError = mostrarError && titulo.isBlank(),
                                    trailingIcon = {
                                        if (mostrarError && titulo.isBlank()) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Error",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    supportingText = {
                                        if (mostrarError && titulo.isBlank()) {
                                            Text("El título es obligatorio")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = descripcion,
                                    onValueChange = {
                                        descripcion = it
                                    },
                                    label = {
                                        Text("Descripción")
                                    },
                                    isError = mostrarError && descripcion.isBlank(),
                                    trailingIcon = {
                                        if (mostrarError && descripcion.isBlank()) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Error",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    supportingText = {
                                        if (mostrarError && descripcion.isBlank()) {
                                            Text("La descripción es obligatoria")
                                        }
                                    },
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (mostrarError) {
                                    Text(
                                        "Completa todos los campos obligatorios.",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (titulo.isNotBlank() && descripcion.isNotBlank()) {
                                            modelo.registrarAvance(
                                                hito.id,
                                                titulo,
                                                descripcion
                                            )
                                            titulo = ""
                                            descripcion = ""
                                            mostrarError = false
                                            mostrarFormulario = false
                                        } else {
                                            mostrarError = true
                                        }
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
