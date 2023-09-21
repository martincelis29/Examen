package iplacex.examen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import iplacex.examen.database.Lugares
import iplacex.examen.database.LugaresDB
import iplacex.examen.pantallas.AgregarOEditarUI
import iplacex.examen.pantallas.CamaraUI
import iplacex.examen.pantallas.InformacionUI
import iplacex.examen.pantallas.ListadoUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    lateinit var cameraController: LifecycleCameraController

    val Permisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        when {
            (it[android.Manifest.permission.ACCESS_COARSE_LOCATION]
                ?: false) or (it[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false) -> {
                Log.v("callback RequestMultiplePermissions", "permiso ubicacion granted")
            }

            (it[android.Manifest.permission.CAMERA] ?: false) -> {
                Log.v("callback RequestMultiplePermissions", "permiso camara granted")
            }

            else -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        setContent {
            Main(Permisos, cameraController)
        }
    }
}

enum class Pantalla {
    Listado, Informacion, Agregar, Editar, Camara
}

@Composable
fun Main(
    permisos: ActivityResultLauncher<Array<String>>, cameraController: LifecycleCameraController
) {
    val contexto = LocalContext.current
    val dao = remember { LugaresDB.getInstance(contexto).LugaresDao() }
    val coroutineScope = rememberCoroutineScope()

    val (lugares, setLugares) = remember { mutableStateOf(emptyList<Lugares>()) }
    val (seleccion, setSeleccion) = remember { mutableStateOf<Lugares?>(null) }
    val (pantalla, setPantalla) = remember { mutableStateOf(Pantalla.Listado) }

    LaunchedEffect(lugares) {
        withContext(Dispatchers.IO) {
            setLugares(dao.obtenerTodos())
            Log.v("Examen", "LaunchedEffect()")
        }
    }

    permisos.launch(
        arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    val onSave = {
        setLugares(dao.obtenerTodos())
        setPantalla(Pantalla.Listado)
    }

    when (pantalla) {
        Pantalla.Agregar -> AgregarOEditarUI(
            null,
            onSave,
            onBack = {
                setPantalla(Pantalla.Listado)
            },
        )

        Pantalla.Editar -> AgregarOEditarUI(
            seleccion,
            onSave,
            onBack = {
                setPantalla(Pantalla.Listado)
            },
        )

        Pantalla.Camara -> CamaraUI(
            seleccion, onSave, onBack = {
                setPantalla(Pantalla.Listado)
            }, permisos, cameraController
        )

        Pantalla.Informacion -> InformacionUI(seleccion, onBack = {
            setPantalla(Pantalla.Listado)
        }, onEdit = {
            setPantalla(Pantalla.Editar)
            setSeleccion(seleccion)
        }, onDelete = {
            coroutineScope.launch(Dispatchers.IO) {
                val dao = LugaresDB.getInstance(contexto).LugaresDao()
                if (seleccion != null) {
                    dao.eliminar(seleccion)
                }
                onSave()
            }
        }, onCamera = {
            setPantalla(Pantalla.Camara)
        })

        else -> ListadoUI(lugares, onAdd = {
            setPantalla(Pantalla.Agregar)
        }, onEdit = { lugares ->
            setPantalla(Pantalla.Editar)
            setSeleccion(lugares)
        }, onInfo = { lugar ->
            setPantalla(Pantalla.Informacion)
            setSeleccion(lugar)
        }, onDelete = { lugar ->
            coroutineScope.launch(Dispatchers.IO) {
                dao.eliminar(lugar)
                setLugares(dao.obtenerTodos())
            }
        })
    }
}