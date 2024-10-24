package hu.bme.aut.android.examapp

expect class TextRecognitionAnalyzer(onDetectedTextUpdated: (String) -> Unit) {
    companion object {
    }

}