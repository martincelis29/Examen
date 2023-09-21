package iplacex.examen.pantallas

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import iplacex.examen.R
import iplacex.examen.database.Lugares
import iplacex.examen.database.LugaresDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarOEditarUI(
    lugar: Lugares?,
    onSave: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val (nombre, setNombre) = remember { mutableStateOf(lugar?.nombre ?: "") }
    val (imagen, setImagen) = remember { mutableStateOf(lugar?.imagen ?: "") }
    val (latitud, setLatitud) = remember { mutableStateOf(lugar?.latitud ?: 0.0) }
    val (longitud, setLongitud) = remember { mutableStateOf(lugar?.longitud ?: 0.0) }
    val (orden, setOrden) = remember { mutableStateOf(lugar?.orden ?: 0) }
    val (costoAlojamiento, setCostoAlojamiento) = remember {
        mutableStateOf(
            lugar?.costoAlojamiento ?: 0
        )
    }
    val (costoTraslados, setCostoTraslados) = remember {
        mutableStateOf(
            lugar?.costoTraslados ?: 0
        )
    }
    val (comentarios, setComentarios) = remember { mutableStateOf(lugar?.comentarios ?: "") }

    val txt_lugar = contexto.getString(R.string.lugar)
    val txt_imagen = contexto.getString(R.string.imagen)
    val txt_orden = contexto.getString(R.string.orden)
    val txt_latitud = contexto.getString(R.string.latitud)
    val txt_longitud = contexto.getString(R.string.longitud)
    val txt_costo_alojamiento = contexto.getString(R.string.costo_alojamiento)
    val txt_costo_traslados = contexto.getString(R.string.costo_traslados)
    val txt_comentarios = contexto.getString(R.string.comentarios)
    val txt_alerta_agregar_o_editar = contexto.getString(R.string.alerta_agregar_o_editar)
    val txt_agregar = contexto.getString(R.string.agregar)
    val txt_guardar = contexto.getString(R.string.guardar)
    val txt_eliminar = contexto.getString(R.string.eliminar)
    val txt_obtener_ubicacion = contexto.getString(R.string.obtener_ubicacion)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            Text(
                text = "${txt_lugar}*",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                value = nombre,
                onValueChange = { setNombre(it) },
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = txt_imagen,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                value = imagen,
                onValueChange = { setImagen(it) },
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${txt_orden}*",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                value = orden.toString(), onValueChange = {
                    val nuevoOrden = it.toIntOrNull() ?: 0
                    setOrden(nuevoOrden)
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = txt_latitud,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                value = latitud.toString(), onValueChange = {
                    val nuevaLatitud = it.toDoubleOrNull() ?: 0.0
                    setLatitud(nuevaLatitud)
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = txt_longitud,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                value = longitud.toString(), onValueChange = {
                    val nuevaLongitud = it.toDoubleOrNull() ?: 0.0
                    setLongitud(nuevaLongitud)
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = txt_costo_alojamiento,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                value = costoAlojamiento.toString(), onValueChange = {
                    val nuevoCostoAlojamiento = it.toIntOrNull() ?: 0
                    setCostoAlojamiento(nuevoCostoAlojamiento)
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = txt_costo_traslados,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                value = costoTraslados.toString(), onValueChange = {
                    val nuevoCostoTraslados = it.toIntOrNull() ?: 0
                    setCostoTraslados(nuevoCostoTraslados)
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = txt_comentarios,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(value = comentarios, onValueChange = {
                setComentarios(it)
            })
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                Button(
                    onClick = {
                        if (nombre.isNotEmpty() && orden > 0) {
                            coroutineScope.launch(Dispatchers.IO) {
                                val dao = LugaresDB.getInstance(contexto).LugaresDao()
                                val lugar = Lugares(
                                    lugar?.id ?: 0,
                                    nombre,
                                    imagen,
                                    latitud,
                                    longitud,
                                    orden,
                                    costoAlojamiento,
                                    costoTraslados,
                                    comentarios,
                                )
                                if (lugar.id > 0) {
                                    dao.actualizar(lugar)
                                } else {
                                    dao.insertar(lugar)
                                }
                                onSave()
                            }
                        } else {
                            coroutineScope.launch(Dispatchers.IO) {
                                snackbarHostState.showSnackbar(txt_alerta_agregar_o_editar)
                            }
                        }
                    },
                ) {
                    var textoGuardar = (txt_agregar)
                    if (lugar?.id ?: 0 > 0) {
                        textoGuardar = (txt_guardar)
                    }
                    Text(textoGuardar)
                }
                if (lugar?.id ?: 0 > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            val dao = LugaresDB.getInstance(contexto).LugaresDao()
                            if (lugar != null) {
                                dao.eliminar(lugar)
                            }
                            onSave()
                        }
                    }) {
                        Text(txt_eliminar)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = Icons.Filled.ArrowBack,
            tint = Color.Gray,
            modifier = Modifier
                .clickable { onBack() }
                .size(32.dp),
            contentDescription = "Volver")
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.End
    ) {
        SmallFloatingActionButton(
            onClick = {
                conseguirUbicacion(contexto) {
                    setLatitud(it.latitude)
                    setLongitud(it.longitude)
                }
            },
        ) {
            Row {
                Icon(
                    imageVector = Icons.Filled.Place, contentDescription = ""
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = " ${txt_obtener_ubicacion}   ",
                )
            }
        }
    }
}

class SinPermisoException(mensaje: String) : Exception(mensaje)

fun conseguirUbicacion(
    contexto: Context, onPermisoUbicacionOk: (location: Location) -> Unit
) {
    try {
        val servicio = LocationServices.getFusedLocationProviderClient(contexto)
        val tarea = servicio.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        tarea.addOnSuccessListener { location ->
            if (location != null) {
                Log.v("conseguirUbicacion", "Ubicación conseguida")
                onPermisoUbicacionOk(location)
            } else {
                Log.v("conseguirUbicacion", "Ubicación nula")
            }
        }
    } catch (e: SecurityException) {
        throw SinPermisoException(e.message ?: "No tiene permisos para conseguir la ubicación")
    }
}
