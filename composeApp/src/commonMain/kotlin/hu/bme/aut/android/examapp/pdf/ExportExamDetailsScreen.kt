package hu.bme.aut.android.examapp.pdf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsViewModel

@Composable
expect fun ExportExamDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    examViewModel: ExamDetailsViewModel = viewModel { ExamDetailsViewModel(savedStateHandle) }
)