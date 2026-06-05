package com.example.animon.feature.details.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.core.designsystem.AnimonTileBeige
import com.example.animon.core.designsystem.AnimonTileGreen
import com.example.animon.feature.details.viewmodel.AnimalDetailsViewModel
import com.example.animon.feature.details.viewmodel.MedicalRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MedicalInfoContent(
    records: List<MedicalRecord>,
    isVet: Boolean,
    viewModel: AnimalDetailsViewModel,
    navController: NavController,
    onAddNewRecord: (String, String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddMedicalRecordDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, desc, date ->
                onAddNewRecord(title, desc, date)
                showDialog = false
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isVet) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnimonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "+ Dodaj nowy wpis medyczny", color = AnimonBeige, fontWeight = FontWeight.Bold)
            }
        }

        if (records.isEmpty()) {
            Text(
                text = "Brak wpisów medycznych dla tego zwierzęcia.",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = records,
                    key = { record -> record.id }
                ) { record ->
                    val isAuthor = viewModel.isCurrentUserAuthor(record.vetId)

                    MedicalInfoTile(
                        title = record.title,
                        date = record.date,
                        description = record.description,
                        vetId = record.vetId,
                        vetName = record.vetName,
                        isAuthor = isAuthor,
                        navController = navController,
                        onDelete = { viewModel.deleteMedicalRecord(record.id) },
                        onUpdate = { newTitle, newDesc -> viewModel.updateMedicalRecord(record.id, newTitle, newDesc) }
                    )
                }
            }
        }

    }
}

@Composable
fun MedicalInfoTile(
    title: String,
    date: String,
    description: String,
    vetId: String,
    vetName: String,
    isAuthor: Boolean,
    onDelete: () -> Unit,
    onUpdate: (String, String) -> Unit,
    navController: NavController
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditMedicalRecordDialog(
            initialTitle = title,
            initialDescription = description,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedTitle, updatedDesc ->
                onUpdate(updatedTitle, updatedDesc)
                showEditDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AnimonTileGreen,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
                    tint = AnimonTileBeige,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = AnimonTileBeige,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = date,
                fontSize = 14.sp,
                color = AnimonTileBeige.copy(alpha = 0.8f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AnimonTileBeige.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = description.ifEmpty { "Brak dodatkowego opisu dla tego wpisu." },
                    fontSize = 14.sp,
                    color = AnimonTileBeige.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )

                if (isAuthor) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { showEditDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = AnimonTileBeige.copy(alpha = 0.5f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AnimonTileBeige
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            ),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edytuj",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Edytuj",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        OutlinedButton(
                            onClick = { onDelete() },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xFFE57373).copy(alpha = 0.6f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFE57373)
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            ),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Usuń",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Usuń",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (vetName.isNotEmpty() && vetId.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .background(
                                color = AnimonBeige.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                navController.navigate("profile/$vetId")
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentInd,
                            contentDescription = "Profil lekarza",
                            tint = AnimonTileBeige,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Lekarz prowadzący: $vetName",
                            fontSize = 13.sp,
                            color = AnimonTileBeige,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddMedicalRecordDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, date: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val currentDate = remember {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Dodaj wpis medyczny", fontWeight = FontWeight.Bold, color = AnimonGreen) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytuł wpisu") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis dolegliwości / zalecenia") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = currentDate,
                    onValueChange = {  },
                    label = { Text("Data") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title, description, currentDate) },
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
fun EditMedicalRecordDialog(
    initialTitle: String,
    initialDescription: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edytuj wpis medyczny", fontWeight = FontWeight.Bold, color = AnimonGreen) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytuł wpisu") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis dolegliwości / zalecenia") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title, description) },
                colors = ButtonDefaults.buttonColors(containerColor = AnimonGreen)
            ) {
                Text("Zapisz zmiany", color = AnimonBeige)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = AnimonGreen)
            }
        }
    )
}