
package cl.duoc.tutorvirtual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cl.duoc.tutorvirtual.ui.screens.GuiaMetodologicaScreen
import cl.duoc.tutorvirtual.ui.screens.FaqScreen
import cl.duoc.tutorvirtual.ui.screens.AvancesScreen
import cl.duoc.tutorvirtual.ui.screens.PerfilScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                TutorVirtualApp()
            }
        }
    }
}

@Composable
fun TutorVirtualApp() {

    val navController = rememberNavController()

    val pantallaActual by navController.currentBackStackEntryAsState()
    val ruta = pantallaActual?.destination?.route

    Scaffold(
        topBar = {
            Surface(tonalElevation = 3.dp) {
                Text(
                    text = "Tutor Virtual ESUCAR",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        },

        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = ruta == "inicio",
                    onClick = { navController.navigate("inicio") },
                    icon = { Text("I") },
                    label = { Text("Inicio") }
                )

                NavigationBarItem(
                    selected = ruta == "guia",
                    onClick = { navController.navigate("guia") },
                    icon = { Text("G") },
                    label = { Text("Guía") }
                )

                NavigationBarItem(
                    selected = ruta == "faq",
                    onClick = { navController.navigate("faq") },
                    icon = { Text("?") },
                    label = { Text("FAQ") }
                )

                NavigationBarItem(
                    selected = ruta == "avances",
                    onClick = { navController.navigate("avances") },
                    icon = { Text("A") },
                    label = { Text("Avances") }
                )

                NavigationBarItem(
                    selected = ruta == "perfil",
                    onClick = { navController.navigate("perfil") },
                    icon = { Text("P") },
                    label = { Text("Perfil") }
                )
            }
        }

    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier.padding(padding)
        ) {

            composable("inicio") {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 880.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        "Bienvenido al Tutor Virtual",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text("Apoyo para Trabajo de Título I, II y III.")

                    Button(
                        onClick = { navController.navigate("guia") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guía metodológica")
                    }

                    Button(
                        onClick = { navController.navigate("faq") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Preguntas frecuentes")
                    }

                    Button(
                        onClick = { navController.navigate("avances") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mis avances")
                    }

                    Button(
                        onClick = { navController.navigate("perfil") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mi perfil")
                    }
                }
                }
            }

            composable("guia") {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 880.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    GuiaMetodologicaScreen()
                }
                }
            }

            composable("faq") {
                FaqScreen()
            }

            composable("avances") {
                AvancesScreen()
            }

            composable("perfil") {
                PerfilScreen()
            }
        }
    }
}
