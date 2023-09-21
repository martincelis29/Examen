package iplacex.examen.pantallas

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import iplacex.examen.database.Lugares
import java.io.File
import java.time.LocalDateTime

@Composable
fun CamaraUI(
    lugar: Lugares?,
    onSave: () -> Unit = {},
    onBack: () -> Unit = {},
    permisos: ActivityResultLauncher<Array<String>>,
    cameraController: LifecycleCameraController,
) {
    permisos.launch(
        arrayOf(
            android.Manifest.permission.CAMERA,
        )
    )
    Box {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = {
            PreviewView(it).apply {
                controller = cameraController
            }
        })
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(38.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            FloatingActionButton(
                onClick = {
                    onSave()
                }, shape = CircleShape
            ) {

            }
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

fun tomarFotografia(
    cameraController: LifecycleCameraController,
    archivo: File,
    contexto: Context,
    imagenGuardada: (uri: Uri) -> Unit
) {
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(archivo).build()

    cameraController.takePicture(outputFileOptions,
        ContextCompat.getMainExecutor(contexto),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.also {
                    Log.v("tomarFotografia()::onImageSaved", "Foto guardada en ${it.toString()}")
                    imagenGuardada(it)
                }
            }

            override fun onError(e: ImageCaptureException) {
                Log.e("tomarFotografia()", "Error: ${e.message}")
            }
        })
}

fun crearImagen(contexto: Context): File {
    val nombre = LocalDateTime.now().toString().replace(Regex("[T:.-]"), "").substring(0, 14)
    val directorio: File? = contexto.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imagen = File(directorio, "IMG_${nombre}.jpg")
    return imagen
}

fun uri2imageBitmap(uri: Uri, contexto: Context) = BitmapFactory.decodeStream(
    contexto.contentResolver.openInputStream(uri)
).asImageBitmap()