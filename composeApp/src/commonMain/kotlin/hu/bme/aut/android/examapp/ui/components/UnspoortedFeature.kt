package hu.bme.aut.android.examapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.not_supported
import examapp.composeapp.generated.resources.try_another_device
import examapp.composeapp.generated.resources.warning
import org.jetbrains.compose.resources.stringResource

@Composable
fun UnsupportedFeatureScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Warning icon
            Icon(
                Icons.Filled.Warning,
                contentDescription = stringResource(Res.string.warning),
                tint = Color(0xFFE57373),  // Light red color for the icon
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Title text
            Text(
                text = stringResource(Res.string.not_supported),
                fontSize = 20.sp,
                color = Color(0xFF333333),  // Dark gray for the text
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Subtext (optional)
            Text(
                text = stringResource(Res.string.try_another_device),
                fontSize = 16.sp,
                color = Color(0xFF666666),  // Lighter gray for the subtext
                fontWeight = FontWeight.Normal
            )
        }
    }
}