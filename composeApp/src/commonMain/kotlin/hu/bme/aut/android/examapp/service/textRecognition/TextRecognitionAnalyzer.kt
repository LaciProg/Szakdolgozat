package hu.bme.aut.android.examapp.service.textRecognition

expect class TextRecognitionAnalyzer(onDetectedTextUpdated: (String) -> Unit) {
    companion object {
    }

}