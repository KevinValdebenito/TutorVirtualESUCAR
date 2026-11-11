
package cl.duoc.tutorvirtual.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.ui.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen() {

    val modelo: PerfilViewModel = viewModel()

    val perfil by modelo.perfil.collectAsState()
    val direccionGuardada by modelo.direccion.collectAsState()
    val guardado by modelo.guardado.collectAsState()
    val error by modelo.error.collectAsState()

    var nombre by remember(perfil.nombre) {
        mutableStateOf(perfil.nombre)
    }

    var correo by remember(perfil.email) {
        mutableStateOf(perfil.email)
    }

    var direccion by remember(direccionGuardada) {
        mutableStateOf(direccionGuardada)
    }

    var clave by remember {
        mutableStateOf("")
    }

    var mostrarErrores by remember {
        mutableStateOf(false)
    }

    val nombreIncorrecto = mostrarErrores && nombre.isBlank()

    val correoIncorrecto = mostrarErrores &&
            !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()

    val direccionIncorrecta = mostrarErrores && direccion.isBlank()

    val claveIncorrecta = mostrarErrores && clave.length < 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "Mi perfil",
            style = MaterialTheme.typography.headlineSmall
        )

        FotoPerfil()

        HorizontalDivider()

        if (guardado) {

            Text(
                "Perfil guardado correctamente",
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text("Nombre: ${perfil.nombre}")
                    Text("Correo: ${perfil.email}")
                    Text("Dirección: $direccionGuardada")
                    Text("RUT: ${perfil.rut}")
                    Text("Grado: ${perfil.grado}")
                    Text("Asignatura: ${perfil.asignatura}")
                    Text("Profesor guía: ${perfil.profesorGuia}")
                }
            }

            Button(
                onClick = {
                    clave = ""
                    mostrarErrores = false
                    modelo.volverEditar()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a editar")
            }

        } else {

            Text("Completa tus datos personales")

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreIncorrecto,
                supportingText = {
                    if (nombreIncorrecto) {
                        Text("El nombre es obligatorio")
                    }
                }
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = correoIncorrecto,
                supportingText = {
                    if (correoIncorrecto) {
                        Text("Ingresa un correo válido")
                    }
                }
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                isError = direccionIncorrecta,
                supportingText = {
                    if (direccionIncorrecta) {
                        Text("La dirección es obligatoria")
                    }
                }
            )

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Clave de prueba") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                isError = claveIncorrecta,
                supportingText = {
                    if (claveIncorrecta) {
                        Text("Mínimo 6 caracteres")
                    } else {
                        Text("Solo para validar; no se guarda")
                    }
                }
            )

            HorizontalDivider()

            Text(
                "Información académica",
                style = MaterialTheme.typography.titleMedium
            )

            Text("RUT: ${perfil.rut}")
            Text("Grado: ${perfil.grado}")
            Text("Asignatura: ${perfil.asignatura}")

            if (error.isNotEmpty()) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {

                    mostrarErrores = true

                    val nombreValido = nombre.isNotBlank()

                    val correoValido = Patterns.EMAIL_ADDRESS
                        .matcher(correo.trim())
                        .matches()

                    val direccionValida = direccion.isNotBlank()

                    val claveValida = clave.length >= 6

                    if (nombreValido &&
                        correoValido &&
                        direccionValida &&
                        claveValida
                    ) {
                        modelo.guardar(
                            nombre,
                            correo,
                            direccion
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar perfil")
            }
        }
    }
}