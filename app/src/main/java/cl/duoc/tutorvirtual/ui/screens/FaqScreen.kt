
package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.ui.viewmodel.FaqViewModel

@Composable
fun FaqScreen() {

    val modelo: FaqViewModel = viewModel()

    val preguntas by modelo.preguntas.collectAsState()
    val categorias by modelo.categorias.collectAsState()
    val cargando by modelo.cargando.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Todas") }
    var mostrarMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Preguntas frecuentes",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        // BUSCADOR
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar pregunta") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CATEGORIAS
        Box {

            OutlinedButton(
                onClick = { mostrarMenu = true }
            ) {
                Text("Categoría: $categoria")
            }

            DropdownMenu(
                expanded = mostrarMenu,
                onDismissRequest = { mostrarMenu = false }
            ) {
                categorias.forEach { item ->

                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            categoria = item
                            mostrarMenu = false
                            modelo.buscar(busqueda, categoria)
                        }
                    )
                }
            }
        }

        // BOTON BUSCAR
        Button(
            onClick = {
                modelo.buscar(busqueda, categoria)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // RESULTADOS
        if (cargando) {

            CircularProgressIndicator()

        } else if (preguntas.isEmpty()) {

            Text("No se encontraron preguntas.")

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(preguntas, key = { it.id }) { pregunta ->

                    var expandido by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandido = !expandido }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = pregunta.categoria,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge
                                )

                                Icon(
                                    imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (expandido) "Colapsar" else "Expandir",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = pregunta.pregunta,
                                style = MaterialTheme.typography.titleMedium
                            )

                            AnimatedVisibility(
                                visible = expandido,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = pregunta.respuesta,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
