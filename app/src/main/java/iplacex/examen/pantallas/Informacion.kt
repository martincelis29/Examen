package iplacex.examen.pantallas

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import iplacex.examen.R
import iplacex.examen.database.Lugares
import iplacex.examen.webservice.Dolar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable

fun InformacionUI(
    lugar: Lugares?,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onCamera: () -> Unit = {}
) {
    val contexto = LocalContext.current
    val scrollState = rememberScrollState()

    val nombre = remember { mutableStateOf(lugar?.nombre ?: "") }
    val imagen = remember { mutableStateOf(lugar?.imagenUrl ?: "") }
    val latitud = remember { mutableStateOf(lugar?.latitud ?: 0.0) }
    val longitud = remember { mutableStateOf(lugar?.longitud ?: 0.0) }
    val costoAlojamiento = remember { mutableStateOf(lugar?.costoAlojamiento ?: 0.0) }
    val costoTraslados = remember { mutableStateOf(lugar?.costoTraslados ?: 0.0) }
    val comentarios = remember { mutableStateOf(lugar?.comentarios ?: "") }

    val (valorDolar, setValorDolar) = remember { mutableStateOf(0.0) }
    val costoAlojamientoDouble = costoAlojamiento.value.toDouble()
    val costoTrasladosDouble = costoTraslados.value.toDouble()
    val costoAlojamientoDolar = costoAlojamientoDouble / valorDolar
    val costoTrasladosDolar = costoTrasladosDouble / valorDolar

    val txt_costo_por_noche = contexto.getString(R.string.costo_por_noche)
    val txt_traslados = contexto.getString(R.string.traslados)
    val txt_comentarios = contexto.getString(R.string.comentarios)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val service = Dolar.getDolarService()
                val response = service.buscar("dolar")
                setValorDolar(response.dolar.valor)
            } catch (e: Exception) {
                Log.e("Main2", "Error: $e")
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = nombre.value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier.widthIn(min = 250.dp, max = 250.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imagen.value, contentDescription = "Imagen Referencia"
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row {
            Column {
                Text(
                    text = txt_costo_por_noche,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "$${costoAlojamiento.value}",
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%.2fUSD", costoAlojamientoDolar),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )
                }
            }
            Spacer(modifier = Modifier.width(50.dp))
            Column {
                Text(
                    text = txt_traslados,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "$${costoTraslados.value}",
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%.2fUSD", costoTrasladosDolar),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        Column {
            Text(
                text = txt_comentarios,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = comentarios.value,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 30.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            Icon(
                Icons.Filled.AddCircle,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)
                    .clickable { onCamera() },
                contentDescription = "Camara"
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                Icons.Filled.Edit,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)
                    .clickable { onEdit() },
                contentDescription = "Editar"
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                Icons.Filled.Delete,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)
                    .clickable { onDelete() },
                contentDescription = "Eliminar"
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(min = 250.dp, max = 250.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(modifier = Modifier.size(400.dp), factory = {
                MapView(it).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    Configuration.getInstance().userAgentValue = contexto.packageName
                    controller.setZoom(15.0)
                }
            }, update = {
                it.overlays.removeIf { true }
                it.invalidate()

                it.controller.setZoom(18.0)
                val geoPoint = GeoPoint(latitud.value, longitud.value)
                it.controller.animateTo(geoPoint)

                val marcador = Marker(it)
                marcador.position = geoPoint
                marcador.setAnchor(
                    Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER
                )
                it.overlays.add(marcador)
            })
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
}