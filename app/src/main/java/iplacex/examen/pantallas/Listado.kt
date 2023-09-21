package iplacex.examen.pantallas

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import iplacex.examen.R
import iplacex.examen.database.Lugares
import iplacex.examen.webservice.Dolar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ListadoUI(
    lugar: List<Lugares>,
    onAdd: () -> Unit = {},
    onEdit: (l: Lugares) -> Unit = {},
    onInfo: (l: Lugares) -> Unit = {},
    onDelete: (l: Lugares) -> Unit = {}
) {
    val contexto = LocalContext.current

    val txt_agregar_lugar = contexto.getString(R.string.agregar_lugar)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(lugar) { lugar ->
                Lugar(lugar,
                    onClick = { onInfo(lugar) },
                    onEdit = { onEdit(lugar) },
                    onDelete = { onDelete(lugar) })
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        SmallFloatingActionButton(
            onClick = {
                onAdd()
            },
        ) {
            Row {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Agregar Lugar"
                )
                Text(
                    modifier = Modifier.align(CenterVertically),
                    text = " ${txt_agregar_lugar}   ",
                )
            }
        }
    }
}

@Composable
fun Lugar(
    lugares: Lugares, onClick: () -> Unit = {}, onEdit: () -> Unit = {}, onDelete: () -> Unit = {}
) {
    val costoAlojamiento = remember { mutableStateOf(lugares.costoAlojamiento ?: 0.0) }
    val costoTraslados = remember { mutableStateOf(lugares.costoTraslados ?: 0.0) }
    val contexto = LocalContext.current

    val (valorDolar, setValorDolar) = remember { mutableStateOf(0.0) }
    val costoAlojamientoDouble = costoAlojamiento.value.toDouble()
    val costoTrasladosDouble = costoTraslados.value.toDouble()
    val costoAlojamientoDolar = costoAlojamientoDouble / valorDolar
    val costoTrasladosDolar = costoTrasladosDouble / valorDolar

    val txt_costo_por_noche = contexto.getString(R.string.costo_por_noche)
    val txt_traslados = contexto.getString(R.string.traslados)

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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .height(100.dp)
    ) {
        Row(verticalAlignment = CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }) {
            Spacer(
                modifier = Modifier.height(70.dp)
            )
            Box(
                modifier = Modifier.widthIn(min = 100.dp, max = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = lugares.imagenUrl,
                    contentDescription = "Imagen",
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                val nombre = if (lugares.nombre.length > 22) {
                    "${lugares.nombre.substring(0, 22)}..."
                } else {
                    lugares.nombre
                }
                Text(
                    nombre,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Text(
                        text = txt_costo_por_noche,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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
                Row {
                    Text(
                        text = txt_traslados,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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
            Spacer(modifier = Modifier.width(20.dp))
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.Edit,
                tint = Color.Gray,
                modifier = Modifier
                    .height(25.dp)
                    .width(25.dp)
                    .clickable { onEdit() },
                contentDescription = "Editar"
            )
            Icon(
                Icons.Filled.Delete,
                tint = Color.Red,
                modifier = Modifier
                    .height(25.dp)
                    .width(25.dp)
                    .clickable { onDelete() },
                contentDescription = "Eliminar"
            )
        }
    }
}