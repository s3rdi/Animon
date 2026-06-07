package com.example.animon.feature.home.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonDarkGreen
import com.example.animon.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onAnimalClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddAnimalDialog by remember { mutableStateOf(false) }

    val availableSectors = uiState.animals
        .map { it.location }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()

    var filteredAnimals = if (uiState.selectedSector == null) {
        uiState.animals
    } else {
        uiState.animals.filter { it.location == uiState.selectedSector }
    }

    if (searchQuery.isNotBlank()) {
        val query = searchQuery.lowercase().trim()
        filteredAnimals = filteredAnimals.filter { animal ->
            animal.name.lowercase().contains(query) ||
                    animal.species.lowercase().contains(query) ||
                    animal.location.lowercase().contains(query)
        }
    }

    val groupedAnimals = filteredAnimals.groupBy { it.location }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ExpandableTopBar(
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchActiveChange = { isActive ->
                    isSearchActive = isActive
                    if (!isActive) searchQuery = ""
                },
                onSearchQueryChange = { query -> searchQuery = query }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AnimonDarkGreen
                    )
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "Nieznany błąd",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        item(span = { GridItemSpan(3) }) {
                            Spacer(modifier = Modifier.height(8.dp))

                            SectorDropdown(
                                sectors = availableSectors,
                                selectedSector = uiState.selectedSector,
                                onSectorSelected = { newSector -> viewModel.onSectorSelected(newSector) }
                            )
                        }

                        groupedAnimals.forEach { (sectorName, animalsInSector) ->
                            item(span = { GridItemSpan(3) }) {
                                SectionHeader(title = sectorName)
                            }

                            items(animalsInSector.size) { index ->
                                val animal = animalsInSector[index]
                                AnimalCard(
                                    name = animal.name,
                                    photo = animal.photo,
                                    statusColor = animal.status.color,
                                    onClick = { onAnimalClick(animal.id) }
                                    )
                            }
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = { showAddAnimalDialog = true },
                containerColor = AnimonDarkGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Wyrównanie do prawego dolnego rogu
                    .padding(end = 16.dp, bottom = 4.dp) // Zmniejszony dolny margines, przesuwa przycisk w dół
                    .size(64.dp) // Ręcznie ustawiony rozmiar (większy niż standard, mniejszy niż Large)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj zwierzę",
                    modifier = Modifier
                        .size(32.dp) // Duża ikona...
                        .graphicsLayer { // ...sztucznie pogrubiona na osiach X i Y
                            scaleX = 1.3f
                            scaleY = 1.3f
                        }
                )
            }
        }
    }

    if (showAddAnimalDialog) {
        AddAnimalDialog(
            onDismiss = { showAddAnimalDialog = false },
            onConfirm = { name, species, location ->
                viewModel.addAnimal(name, species, location)
                showAddAnimalDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableTopBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchActiveChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    if (isSearchActive) {
        TopAppBar(
            title = {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text("Szukaj (nazwa, gatunek, wybieg)...", fontSize = 14.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AnimonDarkGreen
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = { onSearchActiveChange(false) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Zamknij wyszukiwanie", tint = AnimonDarkGreen)
                }
            },
            actions = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Wyczyść", tint = AnimonDarkGreen)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = "Lista zwierząt",
                    color = AnimonDarkGreen,
                    fontSize = 22.sp
                )
            },
            actions = {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Szukaj",
                        tint = AnimonDarkGreen
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectorDropdown(
    sectors: List<String>,
    selectedSector: String?,
    onSectorSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedSector ?: "Wszystkie wybiegi...",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AnimonBeige.copy(alpha = 0.5f),
                unfocusedContainerColor = AnimonBeige.copy(alpha = 0.5f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = AnimonDarkGreen,
                unfocusedTextColor = Color.DarkGray
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Wszystkie wybiegi...") },
                onClick = {
                    onSectorSelected(null)
                    expanded = false
                }
            )
            sectors.forEach { sector ->
                DropdownMenuItem(
                    text = { Text(sector) },
                    onClick = {
                        onSectorSelected(sector)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
        Text(
            text = title,
            color = Color.DarkGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
    }
}

@Composable
fun AnimalCard(name: String, photo: String, statusColor: Color, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageResId = remember(photo) {
        context.resources.getIdentifier(photo, "drawable", context.packageName)
    }
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF2F2F2))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            if (photo.isNotEmpty() && imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Zdjęcie zwierzęcia $name",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .border(2.dp, AnimonDarkGreen, CircleShape)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .border(2.dp, AnimonDarkGreen, CircleShape)
                        .background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Pets, contentDescription = null, tint = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .size(14.dp)
                    .offset(x = 2.dp, y = (-2).dp)
                    .background(statusColor, CircleShape)
                    .border(2.dp, Color(0xFFF2F2F2), CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (name.isNotEmpty()) {
            Text(
                text = name,
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AddAnimalDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, species: String, location: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Dodaj nowe zwierzę", color = AnimonDarkGreen, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Imię zwierzaka (np. Simba)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = species,
                    onValueChange = { species = it },
                    label = { Text("Gatunek (np. Lew)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Wybieg (np. Afrykarium)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, species, location) },
                colors = ButtonDefaults.buttonColors(containerColor = AnimonDarkGreen),
                enabled = name.isNotBlank() && species.isNotBlank() && location.isNotBlank()
            ) {
                Text("Zapisz", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = Color.Gray)
            }
        },
        containerColor = Color.White
    )
}