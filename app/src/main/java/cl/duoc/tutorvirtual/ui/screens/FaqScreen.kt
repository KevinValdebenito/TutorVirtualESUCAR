
package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

                items(preguntas) { pregunta ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = pregunta.categoria,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = pregunta.pregunta,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = pregunta.respuesta
                            )
                        }
                    }
                }
            }
        }
    }
}
