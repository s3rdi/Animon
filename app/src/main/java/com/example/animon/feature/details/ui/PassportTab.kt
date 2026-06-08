package com.example.animon.feature.details.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.core.designsystem.AnimonTileBeige
import com.example.animon.core.designsystem.AnimonTileGreen
import com.example.animon.feature.details.viewmodel.AnimalDetailsViewModel
import com.example.animon.feature.details.viewmodel.PassportSection
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

@Composable
fun PassportContent(
    sections: List<PassportSection>,
    isVet: Boolean,
    viewModel: AnimalDetailsViewModel
) {
    var isAddSectionDialogOpen by remember { mutableStateOf(false) }
    var activeSectionForDialog by remember { mutableStateOf<PassportSection?>(null) }
    var activeSectionForFieldDialog by remember { mutableStateOf<PassportSection?>(null) }
    var activeFieldForEdit by remember { mutableStateOf<Pair<String, String>?>(null) }

    var sectionToDelete by remember { mutableStateOf<PassportSection?>(null) }

    sectionToDelete?.let { section ->
        AlertDialog(
            onDismissRequest = { sectionToDelete = null },
            title = {
                 Text(
                     text = "Usuń sekcję paszportu",
                     fontWeight = FontWeight.Bold,
                     color = AnimonGreen
                 )
            },
            text = { Text("Czy na pewno chcesz usunąć sekcję „${section.title}” wraz ze wszystkimi jej wpisami?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePassportSection(section.id)
                        sectionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Usuń", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { sectionToDelete = null }) {
                    Text("Anuluj", color = AnimonGreen)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isVet) {
            Button(
                onClick = { isAddSectionDialogOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnimonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "+ Nowa sekcja paszportu", color = AnimonBeige, fontWeight = FontWeight.Bold)
            }
        }

        if (sections.isEmpty()) {
            Text(
                text = "Brak danych paszportowych dla tego zwierzęcia.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            sections.forEach { section ->
                PassportSection(
                    section = section,
                    isVet = isVet,
                    onEditSection = { activeSectionForDialog = section },
                    onDeleteSection = {
                        sectionToDelete = section
                    },
                    onAddItem = {
                        activeSectionForFieldDialog = section
                        activeFieldForEdit = null
                    }
                ) {
                    section.items.forEach { (label, value) ->
                        PassportRowItem(
                            label = label,
                            value = value,
                            isVet = isVet,
                            onEditClick = {
                                activeSectionForFieldDialog = section
                                activeFieldForEdit = Pair(label, value)
                            },
                            onDeleteClick = {
                                viewModel.deletePassportItem(section.id, label)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (isAddSectionDialogOpen) {
        PassportSectionDialog(
            onDismiss = { isAddSectionDialogOpen = false },
            onConfirm = { title, subtitle ->
                viewModel.addPassportSection(title, subtitle)
                isAddSectionDialogOpen = false
            }
        )
    }

    activeSectionForDialog?.let { section ->
        PassportSectionDialog(
            initialTitle = section.title,
            initialSubtitle = section.subtitle,
            onDismiss = { activeSectionForDialog = null },
            onConfirm = { title, subtitle ->
                viewModel.updatePassportSection(section.id, title, subtitle)
                activeSectionForDialog = null
            }
        )
    }

    activeSectionForFieldDialog?.let { section ->
        PassportItemDialog(
            initialLabel = activeFieldForEdit?.first ?: "",
            initialValue = activeFieldForEdit?.second ?: "",
            isEditing = activeFieldForEdit != null,
            onDismiss = {
                activeSectionForFieldDialog = null
                activeFieldForEdit = null
            },
            onConfirm = { label, value ->
                viewModel.putPassportItem(section.id, label, value)
                activeSectionForFieldDialog = null
                activeFieldForEdit = null
            }
        )
    }
}

@Composable
fun PassportSection(
    section: PassportSection,
    isVet: Boolean,
    onEditSection: () -> Unit,
    onDeleteSection: () -> Unit,
    onAddItem: () -> Unit,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimonTileGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.subtitle,
                        fontSize = 11.sp,
                        color = AnimonTileBeige.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = section.title,
                        fontSize = 18.sp,
                        color = AnimonTileBeige,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isVet) {
                        IconButton(onClick = { onEditSection() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edytuj sekcję", tint = AnimonTileBeige)
                        }
                        IconButton(onClick = { onDeleteSection() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń sekcję", tint = Color(0xFFF44336))
                        }
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
                        tint = AnimonTileBeige
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AnimonTileBeige.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    content()

                    if (isVet) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onAddItem,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AnimonTileBeige),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dodaj wpis")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PassportRowItem(
    label: String,
    value: String,
    isVet: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = AnimonTileBeige.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = AnimonTileBeige,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
        if (isVet) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(24.dp).padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edytuj wpis",
                    tint = AnimonTileBeige.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Usuń wpis",
                    tint = Color(0xFFF44336).copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun PassportSectionDialog(
    initialTitle: String = "",
    initialSubtitle: String = "",
    onDismiss: () -> Unit,
    onConfirm: (title: String, subtitle: String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var subtitle by remember { mutableStateOf(initialSubtitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTitle.isEmpty()) "Dodaj sekcję" else "Edytuj sekcję") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Podtytuł (np. SEKCJA I)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytuł sekcji") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, subtitle) }) { Text("Zapisz") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}

@Composable
fun PassportItemDialog(
    initialLabel: String = "",
    initialValue: String = "",
    isEditing: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (label: String, value: String) -> Unit
) {
    var label by remember { mutableStateOf(initialLabel) }
    var value by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edytuj wpis" else "Dodaj nowy wpis") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Etykieta") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isEditing
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Wartość") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(label, value) }) { Text("Zapisz") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}