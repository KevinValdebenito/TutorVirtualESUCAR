package cl.duoc.tutorvirtual.ui.screens

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
    var preguntaAbierta by remember { mutableStateOf<Long?>(null) }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 880.dp).fillMaxWidth().fillMaxHeight().padding(16.dp)) {
            Text("Preguntas frecuentes", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = busqueda, onValueChange = { busqueda = it },
                label = { Text("Buscar pregunta") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            Box {
                OutlinedButton(onClick = { mostrarMenu = true }) { Text("Categoría: $categoria") }
                DropdownMenu(expanded = mostrarMenu, onDismissRequest = { mostrarMenu = false }) {
                    categorias.forEach { item ->
                        DropdownMenuItem(text = { Text(item) }, onClick = {
                            categoria = item
                            mostrarMenu = false
                            modelo.buscar(busqueda, categoria)
                        })
                    }
                }
            }
            Button(
                onClick = { preguntaAbierta = null; modelo.buscar(busqueda, categoria) },
                enabled = !cargando, modifier = Modifier.fillMaxWidth()
            ) { Text("Buscar") }
            Spacer(Modifier.height(12.dp))
            if (cargando) CircularProgressIndicator()
            else if (preguntas.isEmpty()) Text("No se encontraron preguntas.")
            else LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(preguntas, key = { it.id }) { pregunta ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(pregunta.categoria, color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge)
                            TextButton(
                                onClick = { preguntaAbierta = if (preguntaAbierta == pregunta.id) null else pregunta.id },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(pregunta.pregunta + if (preguntaAbierta == pregunta.id) "  ▲" else "  ▼")
                            }
                            AnimatedVisibility(
                                visible = preguntaAbierta == pregunta.id,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) { Text(pregunta.respuesta, modifier = Modifier.padding(top = 8.dp)) }
                        }
                    }
                }
            }
        }
    }
}
