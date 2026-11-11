
package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.data.model.Avance
import cl.duoc.tutorvirtual.data.model.EstadoHito
import cl.duoc.tutorvirtual.ui.viewmodel.HitoViewModel

@Composable
fun AvancesScreen() {

    val modelo: HitoViewModel = viewModel()

    val hitos by modelo.hitos.collectAsState()
    val avances by modelo.avances.collectAsState()
    val cargando by modelo.cargando.collectAsState()

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

                        if (mostrarFormulario) {

                            OutlinedTextField(
                                value = titulo,
                                onValueChange = {
                                    titulo = it
                                },
                                label = {
                                    Text("Título del avance")
                                },
                                isError = mostrarError &&
                                        titulo.isBlank(),
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
                                isError = mostrarError &&
                                        descripcion.isBlank(),
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (mostrarError) {
                                Text(
                                    "Completa ambos campos.",
                                    color =
                                        MaterialTheme.colorScheme.error
                                )
                            }

                            Button(
                                onClick = {
                                    if (titulo.isNotBlank() &&
                                        descripcion.isNotBlank()
                                    ) {

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
