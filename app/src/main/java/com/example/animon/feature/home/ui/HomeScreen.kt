package com.example.animon.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonDarkGreen

// do usunięcia
data class AnimalMock(
    val name: String,
    val sector: String,
    val hasImage: Boolean
)

@Composable
fun HomeScreen() {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val availableSectors = listOf("Terrarium", "Afrykarium", "Ptaszarnia", "Akwarium", "Wybieg Północny")
    var selectedSector by remember { mutableStateOf<String?>(null) }
    val mockAnimals = remember {
        listOf(
            AnimalMock("Ling-Ling", "Terrarium", true),
            AnimalMock("", "Terrarium", false),
            AnimalMock("", "Terrarium", false),
            AnimalMock("", "Terrarium", false),
            AnimalMock("", "Terrarium", false),
            AnimalMock("", "Terrarium", false),
            AnimalMock("Simba", "Afrykarium", false),
            AnimalMock("", "Afrykarium", false),
            AnimalMock("", "Afrykarium", false)
        )
    }

    val filteredAnimals = if (selectedSector == null) {
        mockAnimals
    } else {
        mockAnimals.filter { it.sector == selectedSector }
    }

    val groupedAnimals = filteredAnimals.groupBy { it.sector }

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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(3) }) {
                Spacer(modifier = Modifier.height(8.dp))

                SectorDropdown(
                    sectors = availableSectors,
                    selectedSector = selectedSector,
                    onSectorSelected = { newSector -> selectedSector = newSector }
                )
            }

            groupedAnimals.forEach { (sectorName, animalsInSector) ->

                item(span = { GridItemSpan(3) }) {
                    SectionHeader(title = sectorName)
                }

                items(animalsInSector.size) { index ->
                    val animal = animalsInSector[index]
                    AnimalCard(name = animal.name, hasImage = animal.hasImage)
                }
            }
        }
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
fun AnimalCard(name: String, hasImage: Boolean) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color(0xFFF2F2F2), RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (hasImage) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = Color.White)
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
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