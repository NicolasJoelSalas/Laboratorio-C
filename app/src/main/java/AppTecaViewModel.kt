package com.example.appteca3
import App
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//Esto hace que el ViewModel tenga un ciclo de vida asociado
// a la pantalla/Activity correspondiente
// y pueda sobrevivir a ciertas recreaciones de la UI.
class AppTecaViewModel : ViewModel() {
    private var query = ""
    private var soloFavoritas = false
    // ── ANTES (3B): MutableLiveData<List<App>>() / LiveData<List<App>>
    // ── AHORA: MutableStateFlow<List<App>> () / StateFlow<List<App>>

    //Un StateFlow representa un valor que puede cambiar y que otros pueden observar.
    //Mutable significa que el ViewModel puede modificar el valor.
    private val _listaVisible = MutableStateFlow<List<App>>(emptyList())
    val listaVisible: StateFlow<List<App>> = _listaVisible
    private val _modoSoloFavoritas = MutableStateFlow(false)
    val modoSoloFavoritas: StateFlow<Boolean> = _modoSoloFavoritas


    private val _appSeleccionada = MutableStateFlow<App?>(null)
    val appSeleccionada: StateFlow<App?> = _appSeleccionada
    fun seleccionar(app: App) { _appSeleccionada.value = app }
    fun volverALista() { _appSeleccionada.value = null }


    init {
        Log.d("VIDA", "ViewModel → creado (${hashCode()})")
        aplicarFiltros()
    }
    fun buscar(texto: String) {
        query = texto.trim()
        aplicarFiltros()


    }
    fun alternarModo() {
        soloFavoritas = !soloFavoritas
        aplicarFiltros()


    }
    fun alternarFavorita(app: App) {
        val nuevas = Catalogo.apps.map {
            if (it.id == app.id) it.copy(esFavorita = !it.esFavorita) else it
        }
        Catalogo.apps.clear()
        Catalogo.apps.addAll(nuevas)
        aplicarFiltros()

    }
    private fun aplicarFiltros() {
        var lista: List<App> = Catalogo.apps.toList()
        if (query.isNotEmpty()) lista = lista.filter {
            it.nombre.contains(query, true) || it.categoria.contains(query,
                true)
        }
        if (soloFavoritas) lista = lista.filter { it.esFavorita }
        _listaVisible.value = lista
        _modoSoloFavoritas.value = soloFavoritas
        _appSeleccionada.value = appSeleccionada.value?.let { sel ->
            Catalogo.apps.find { it.id == sel.id }
        }


    }
    override fun onCleared() {
        Log.d("VIDA", "ViewModel → onCleared")
    }
}
