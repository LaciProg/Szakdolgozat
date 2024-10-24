package hu.bme.aut.android.examapp

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.AspectRatio
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.android.examapp.ui.viewmodel.submission.SubmissionViewModel

@Composable
actual fun CameraScreen(savedStateHandle: SavedStateHandle) {
    CameraContent(savedStateHandle)
}

@Composable
private fun CameraContent(
    savedStateHandle: SavedStateHandle,
    viewModel: SubmissionViewModel = viewModel { SubmissionViewModel(savedStateHandle) }
) {

    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController = remember { LifecycleCameraController(context) }

    var detectedText: String by remember { mutableStateOf("No text detected yet..") }
    var parsedMap by remember { mutableStateOf<Map<Int, Any>>(emptyMap()) }

    // Szöveg frissítésekor a map is frissül
    fun onTextUpdated(updatedText: String) {
        detectedText = updatedText
        parsedMap = parseDetectedText(updatedText, viewModel)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(
            title = { Text("Text Scanner") },
            actions = {
                IconButton(onClick = { viewModel.getExam(viewModel.examId, viewModel.answers) }) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back") }
                IconButton(onClick = { /*TODO*/ }) { Icon(Icons.Rounded.AddCircle, contentDescription = "Add") }
            })
        },
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.BottomCenter
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        startTextRecognition(
                            context = context,
                            cameraController = cameraController,
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            onDetectedTextUpdated = ::onTextUpdated
                        )
                    }
                }
            )

            // Az észlelt szöveg megjelenítése
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(androidx.compose.ui.graphics.Color.White)
                    .padding(16.dp),
                text = "$detectedText\n\nParsed Map: $parsedMap",
            )

        }
    }
}

private fun parseDetectedText(detectedText: String, viewModel: SubmissionViewModel): Map<Int, Any> {
    val map = mutableMapOf<Int, Any>()

    // Sorok mentén bontjuk fel a szöveget
    val lines = detectedText.split("\n")

    // Minden sort feldolgozunk: "1: TRUE" vagy "3: A,B"
    lines.forEach { line ->
        val parts = line.split(":").map { it.trim() } // Kettőspont mentén split, és whitespace eltávolítása

        if (parts.size == 2) {
            val key = parts[0].toIntOrNull() // A kulcs a kettőspont előtti rész (pl. "1")
            val value = parts[1].uppercase() // Az érték a kettőspont utáni rész (pl. "TRUE" vagy "A,B")

            if (key != null) {
                // Ha vesszővel elválasztott betűk vannak (pl. "A,B"), külön kezeljük
                val parsedValue = when {
                    value == "TRUE" -> true
                    value == "FALSE" -> false
                    value.contains(",") -> value.split(",").map { it.trim() }.joinToString(",") // Több betű vesszővel
                    value.all { it.isLetter() && it.isUpperCase() } -> value // Egy betű
                    else -> "INVALID"
                }
                map[key] = parsedValue
                viewModel.answers.answers[key - 1] = parsedValue.toString()
                Log.i("Answers", viewModel.answers.answers.toString())
            }
        }
    }

    return map
}

private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (String) -> Unit
) {

    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated)
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}
