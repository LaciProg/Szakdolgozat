package hu.bme.aut.android.examapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownList(
    modifier: Modifier = Modifier,
    name: String = "Gyümölcsök",
    items: List<String> = listOf("Alma", "Körte", "Banán"),
    default: String = "",
    onChoose: (String) -> Unit = {},
) {
    var isExpanded by remember{ mutableStateOf(false)}
    var item by remember {
      mutableStateOf(default)
    }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        modifier = modifier.fillMaxWidth(),
        onExpandedChange = { newValue ->
            isExpanded = newValue
        }
    ) {
        OutlinedTextField(
            value = item.ifEmpty { default },
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            placeholder = {
                Text(text = name)
            },
            //colors = ExposedDropdownMenuDefaults.textFieldColors(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            },
            modifier = modifier.fillMaxWidth()
        ) {
            for(i in items) {
                DropdownMenuItem(
                    text = {

                        Text(text = i)
                    },
                    onClick = {
                        item = i
                        //Log.d("DropDownList", "i: $i")
                        isExpanded = false
                        onChoose(i)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            DropdownMenuItem(
                text = { Text(text = "") },
                onClick = {
                    item = ""
                    isExpanded = false
                    onChoose("")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
