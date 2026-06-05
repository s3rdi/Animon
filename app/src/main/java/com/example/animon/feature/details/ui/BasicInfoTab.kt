package com.example.animon.feature.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.core.designsystem.AnimonTileBeige
import com.example.animon.core.designsystem.AnimonTileGreen
import com.example.animon.feature.details.viewmodel.AnimalData
import com.example.animon.feature.details.viewmodel.AnimalDetailsViewModel
import com.example.animon.feature.details.viewmodel.AnimalStatus

data class EditableField(
    val label: String,
    val initialValue: String,
    val onCopyUpdated: (AnimalData, String) -> AnimalData
)

@Composable
fun BasicInfoContent(
    animal: AnimalData,
    status: AnimalStatus,
    viewModel: AnimalDetailsViewModel,
    onUpdate: (AnimalData) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf<EditableField?>(null) }

    if (showEditDialog) {
        EditAnimalDetailsDialog(
            animal = animal,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedAnimal ->
                onUpdate(updatedAnimal)
                showEditDialog = false
            }
        )
    }

    editingField?.let { field ->
        EditSingleFieldDialog(
            field = field,
            onDismiss = { editingField = null },
            onConfirm = { newValue ->
                val updatedAnimal = field.onCopyUpdated(animal, newValue)
                viewModel.updateAnimalDocument(updatedAnimal)
                editingField = null
            }
        )
    }

    val infoItems = listOf(
        EditableField("Data urodzenia", animal.date_of_birth) { animal, value -> animal.copy(date_of_birth = value) },
        EditableField("Gatunek", animal.species) { animal, value -> animal.copy(species = value) },
        EditableField("Płeć", animal.gender) { animal, value -> animal.copy(gender = value) },
        EditableField("Wielkość", animal.size) { animal, value -> animal.copy(size = value) },
        EditableField("Kastracja", animal.castration) { animal, value -> animal.copy(castration = value) },
        EditableField("Waga (kg)", animal.weight) { animal, value -> animal.copy(weight = value) },
        EditableField("Temperatura (°C)", animal.temperature) { animal, value -> animal.copy(temperature = value) },
        EditableField("Puls (/min)", animal.pulse) { animal, value -> animal.copy(pulse = value) },
        EditableField("Apetyt", animal.appetite) { animal, value -> animal.copy(appetite = value) },
        EditableField("Szczepienie", animal.rabies_vaccination) { animal, value -> animal.copy(rabies_vaccination = value) }
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            StatusInfoTile(
                status = status,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEditDialog = true }
            )
        }

        items(infoItems.size) { index ->
            val field = infoItems[index]
            InfoTile(
                label = field.label,
                value = field.initialValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { editingField = field }
            )
        }
    }
}

@Composable
fun InfoTile(
    label: String,
    value: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = AnimonTileGreen,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = AnimonTileBeige,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (value == "") "-" else value,
            fontSize = 18.sp,
            color = AnimonTileBeige,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusInfoTile(
    status: AnimalStatus,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = status.color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 2.dp,
                color = status.color,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Stan zdrowia",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = status.icon,
                contentDescription = status.label,
                tint = status.color,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = status.label,
                fontSize = 18.sp,
                color = status.color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EditAnimalDetailsDialog(
    animal: AnimalData,
    onDismiss: () -> Unit,
    onConfirm: (AnimalData) -> Unit
) {
    var location by remember { mutableStateOf(animal.location) }
    var weight by remember { mutableStateOf(animal.weight) }
    var temperature by remember { mutableStateOf(animal.temperature) }
    var pulse by remember { mutableStateOf(animal.pulse) }
    var appetite by remember { mutableStateOf(animal.appetite) }
    var vaccination by remember { mutableStateOf(animal.rabies_vaccination) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edytuj parametry życiowe", fontWeight = FontWeight.Bold, color = AnimonGreen) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokalizacja") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Waga (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = temperature,
                    onValueChange = { temperature = it },
                    label = { Text("Temperatura (°C)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pulse,
                    onValueChange = { pulse = it },
                    label = { Text("Puls (/min)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = appetite,
                    onValueChange = { appetite = it },
                    label = { Text("Apetyt (Tak/Nie)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vaccination,
                    onValueChange = { vaccination = it },
                    label = { Text("Szczepienie przeciw wściekliźnie") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = animal.copy(
                        location = location,
                        weight = weight,
                        temperature = temperature,
                        pulse = pulse,
                        appetite = appetite,
                        rabies_vaccination = vaccination
                    )
                    onConfirm(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimonGreen)
            ) {
                Text("Zapisz", color = AnimonBeige)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = AnimonGreen)
            }
        }
    )
}

@Composable
fun EditSingleFieldDialog(
    field: EditableField,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(field.initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edytuj: ${field.label}", color = AnimonGreen, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = { Text(field.label) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimonGreen,
                    focusedLabelColor = AnimonGreen
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(textValue) },
                colors = ButtonDefaults.buttonColors(containerColor = AnimonGreen)
            ) {
                Text("Zapisz", color = AnimonBeige)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = AnimonGreen)
            }
        }
    )
}