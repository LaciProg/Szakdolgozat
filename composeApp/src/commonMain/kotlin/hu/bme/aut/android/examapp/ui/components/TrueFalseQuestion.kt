package hu.bme.aut.android.examapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ExportedTrueFalseQuestion(number: Int, question: String, point: Double, color: Color = Color.Black)
{
    Column(modifier = Modifier.width(500.dp).fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = "$number. $question", color = color)
            Spacer(modifier = Modifier.padding(end = 16.dp))
            Text(text = "Point: $point\\", modifier = Modifier.padding(end = 50.dp), color = color)
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            Text("True", color = color)
            Spacer(modifier = Modifier.width(25.dp))
            Text("False", color = color)
        }
    }
}
