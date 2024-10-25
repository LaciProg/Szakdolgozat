package hu.bme.aut.android.examapp.pdf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsViewModel

@Composable
actual fun ExportExamDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier,
    savedStateHandle: SavedStateHandle,
    examViewModel: ExamDetailsViewModel
) {
}