package hu.bme.aut.android.examapp.pdf

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.components.UnsupportedFeatureScreen
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsViewModel

@Composable
actual fun ExportExamDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier,
    savedStateHandle: SavedStateHandle,
    examViewModel: ExamDetailsViewModel
) {
    Scaffold(
        topBar = {
            TopAppBarContent(examViewModel.uiState.examDetails.name, navigateBack)
                 },
        ){innerPadding ->
            UnsupportedFeatureScreen(modifier = Modifier.padding(innerPadding))
    }
}