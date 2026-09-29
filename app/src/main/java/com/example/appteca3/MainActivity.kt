package com.example.appteca3

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appteca3.ui.theme.AppTeca3Theme
import androidx.compose.material3.Button
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { //Declara la estructura para trabajar con Compose
            AppTeca3Theme {
                PantallaAppTeca(vm = viewModel())
                //viewModel() obtiene/crea una instancia del AppTecaViewModel asociada al ciclo de vida correspondiente.
            }
        }
    }
}
//"Esta función sirve para construir interfaz gráfica."
@Composable
fun PantallaAppTeca(vm: AppTecaViewModel = viewModel()) { //"Para construir esta pantalla necesito un AppTecaViewModel."

    //Como listavisibe es stateflow,
    // collectAsStateWithLifecycle() Convierte ese Flow/StateFlow en un estado
    // que Compose puede observar.
    val lista by vm.listaVisible.collectAsStateWithLifecycle()
    val modoFav by vm.modoSoloFavoritas.collectAsStateWithLifecycle()

   // mutableStateOf("")
   // Crea un estado observable por Compose.
    var textoBusqueda by rememberSaveable { mutableStateOf("") }

    val seleccionada by vm.appSeleccionada.collectAsStateWithLifecycle()
    if (seleccionada != null) {
        DetalleApp(
            app = seleccionada!!, //"Dame el valor de seleccionada asumiendo que existe."
            onFavoritoClick = { vm.alternarFavorita(seleccionada!!) },
            onVolver = { vm.volverALista() }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize()) { //Una Column organiza elementos verticalmente
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { nuevo ->
                    textoBusqueda = nuevo
                    vm.buscar(nuevo)
                },
                label = { Text("Buscar por nombre o categoría…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Button(
                onClick = { vm.alternarModo() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(if (modoFav) " Solo favoritas" else "☆ Todas")
            }
            ListaApps(
                apps = lista,
                onAppClick = { app -> vm.seleccionar(app) }, //"Si el usuario toca una app, llamá a vm.seleccionar(app)."
                onFavoritoClick = { app -> vm.alternarFavorita(app) }
            )
        }
    }
}

@Composable
fun ListaApps(
    apps: List<App>,
    onAppClick: (App) -> Unit, //"Una función que recibe un App y no devuelve nada."
    onFavoritoClick: (App) -> Unit //"Una función que recibe un App y no devuelve nada."
) {
    LazyColumn { //Es una lista vertical.
        //"Para cada elemento de apps, ejecutá este bloque."
        items(apps, key = { it.id }) { app ->  //{ app "El elemento actual de la lista estará disponible mediante la variable app."

            //por cada App
            //    ↓
            //llamame a esta parte
            //    ↓
            //y dame el App actual en "app"
            FilaApp(
                app = app,
                onClick = { onAppClick(app) },
                onFavoritoClick = { onFavoritoClick(app) }
            )
        }
    }
}

@Composable
fun FilaApp(
    app: App,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    Row( //Row organiza cosas horizontalmente.
        modifier = Modifier
            .fillMaxWidth() //Ocupá todo el ancho disponible
            .clickable { onClick() } //"Si alguien toca esta fila, ejecutá onClick()."
            .padding(16.dp), //Es el ancho
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) { //Column organiza verticalmente:
            Text(app.nombre, style = MaterialTheme.typography.titleMedium) //Column organiza verticalmente
            Text(app.categoria, style = MaterialTheme.typography.bodySmall) //Column organiza verticalmente
        }
        Text( text = if (app.esFavorita) "★" else "☆",
            fontSize = 24.sp,
            modifier = Modifier
                .clickable { onFavoritoClick() }
                .padding(8.dp)
        )
    }
}
@Composable
fun DetalleApp(app: App, onFavoritoClick: () -> Unit, onVolver: () -> Unit) {
    BackHandler { onVolver() }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(app.nombre, style = MaterialTheme.typography.headlineLarge)
        Text(app.categoria, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Text(app.descripcion, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onFavoritoClick) {
            Text(if (app.esFavorita) "★ Quitar de favoritas" else "☆ Marcar favorita")
        }
    }
}





