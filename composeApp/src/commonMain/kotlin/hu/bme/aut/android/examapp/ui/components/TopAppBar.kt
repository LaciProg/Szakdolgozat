package hu.bme.aut.android.examapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(0xFF6200EE),
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Láthatatlan helykitöltő a jobb oldalon, hogy a cím középen maradjon
                Spacer(modifier = Modifier.weight(1f))
                Text(text = title, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))  // Ez szimmetrikusan elosztja a teret
            }
        },
        actions = {
            // Az actions részt akár itt használhatod, ha szükséges
        },
    )
}