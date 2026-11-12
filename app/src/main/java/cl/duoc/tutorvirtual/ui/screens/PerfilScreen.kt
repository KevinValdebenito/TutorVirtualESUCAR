package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.ui.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen() {

    val modelo: PerfilViewModel = viewModel()
    val estado by modelo.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineSmall
        )

        FotoPerfil()

        HorizontalDivider()

        AnimatedVisibility(
            visible = estado.guardadoExitoso,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Text(
                    text = "Perfil guardado correctamente",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Nombre: ${estado.nombre}")
                        Text("Correo: ${estado.correo}")
                        Text("Dirección: ${estado.direccion}")
                        Text("RUT: ${estado.rut}")
                        Text("Grado: ${estado.grado}")
                        Text("Asignatura: ${estado.asignatura}")
                        Text("Profesor guía: ${estado.profesorGuia}")
                    }
                }

                Button(
                    onClick = { modelo.volverEditar() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver a editar")
                }
            }
        }

        AnimatedVisibility(
            visible = !estado.guardadoExitoso,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Text("Completa tus datos personales")

            // Campo Nombre
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = modelo::onNombreChange,
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = estado.errores.nombre != null,
                trailingIcon = {
                    if (estado.errores.nombre != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                supportingText = {
                    estado.errores.nombre?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo Correo
            OutlinedTextField(
                value = estado.correo,
                onValueChange = modelo::onCorreoChange,
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = estado.errores.correo != null,
                trailingIcon = {
                    if (estado.errores.correo != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                supportingText = {
                    estado.errores.correo?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo Dirección
            OutlinedTextField(
                value = estado.direccion,
                onValueChange = modelo::onDireccionChange,
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                isError = estado.errores.direccion != null,
                trailingIcon = {
                    if (estado.errores.direccion != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                supportingText = {
                    estado.errores.direccion?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo Clave
            OutlinedTextField(
                value = estado.clave,
                onValueChange = modelo::onClaveChange,
                label = { Text("Clave de prueba") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = estado.errores.clave != null,
                trailingIcon = {
                    if (estado.errores.clave != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                supportingText = {
                    if (estado.errores.clave != null) {
                        Text(estado.errores.clave!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Solo para validar (mínimo 6 caracteres); no se guarda")
                    }
                }
            )

            HorizontalDivider()

            Text(
                "Información académica institucional",
                style = MaterialTheme.typography.titleMedium
            )

            Text("RUT: ${estado.rut}")
            Text("Grado: ${estado.grado}")
            Text("Asignatura: ${estado.asignatura}")
            Text("Profesor guía: ${estado.profesorGuia}")

            if (estado.errorMensaje.isNotEmpty()) {
                Text(
                    text = estado.errorMensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    if (modelo.validarFormulario()) {
                        modelo.guardar()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar perfil")
            }
        }
    }
}
}
