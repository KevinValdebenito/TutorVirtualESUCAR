package cl.duoc.tutorvirtual

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.tutorvirtual.ui.components.ContenidoAdaptable
import cl.duoc.tutorvirtual.ui.screens.AvancesScreen
import cl.duoc.tutorvirtual.ui.screens.FaqScreen
import cl.duoc.tutorvirtual.ui.screens.GuiaMetodologicaScreen
import cl.duoc.tutorvirtual.ui.screens.PantallaInicio
import cl.duoc.tutorvirtual.ui.screens.PerfilScreen
import cl.duoc.tutorvirtual.ui.theme.TutorVirtualESUCARTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Las barras son transparentes para que Compose dibuje el fondo y respete sus espacios.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )

        setContent {
            TutorVirtualESUCARTheme {
                TutorVirtualApp()
            }
        }
    }
}

private data class DestinoPrincipal(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
)

private val destinosPrincipales = listOf(
    DestinoPrincipal("inicio", "Inicio", Icons.Default.Home),
    DestinoPrincipal("guia", "Guía", Icons.AutoMirrored.Filled.List),
    DestinoPrincipal("faq", "FAQ", Icons.Default.Info),
    DestinoPrincipal("avances", "Avances", Icons.Default.CheckCircle),
    DestinoPrincipal("perfil", "Perfil", Icons.Default.Person)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorVirtualApp() {
    val navController = rememberNavController()
    val pantallaActual by navController.currentBackStackEntryAsState()
    val ruta = pantallaActual?.destination?.route ?: "inicio"
    val tituloPantalla = destinosPrincipales.firstOrNull { it.ruta == ruta }?.etiqueta ?: "Inicio"

    fun navegarA(destino: String) {
        navController.navigate(destino) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tutor Virtual ESUCAR",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tituloPantalla,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                destinosPrincipales.forEach { destino ->
                    NavigationBarItem(
                        selected = ruta == destino.ruta,
                        onClick = { navegarA(destino.ruta) },
                        icon = {
                            Icon(
                                imageVector = destino.icono,
                                contentDescription = destino.etiqueta
                            )
                        },
                        label = { Text(destino.etiqueta) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            composable("inicio") {
                PantallaInicio(onNavigate = ::navegarA)
            }

            composable("guia") {
                ContenidoAdaptable {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        GuiaMetodologicaScreen()
                    }
                }
            }

            composable("faq") { FaqScreen() }
            composable("avances") { AvancesScreen() }
            composable("perfil") { PerfilScreen() }
        }
    }
}
