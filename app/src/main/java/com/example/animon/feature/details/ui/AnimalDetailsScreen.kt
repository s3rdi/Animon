package com.example.animon.feature.details.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animon.R
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.core.designsystem.AnimonTileBeige
import com.example.animon.core.designsystem.AnimonTileGreen
import com.example.animon.feature.details.viewmodel.AnimalData
import com.example.animon.feature.details.viewmodel.AnimalDetailsViewModel
import com.example.animon.feature.details.viewmodel.MedicalRecord
import com.example.animon.feature.details.viewmodel.PassportSection

@Composable
fun AnimalDetailsScreen(
    viewModel: AnimalDetailsViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dane podstawowe", "Dane medyczne", "Paszport")

    val animalData by viewModel.animalState.collectAsState()
    val medicalRecords by viewModel.medicalRecordsState.collectAsState()
    val passportSections by viewModel.passportState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimalImage()

        AnimalHeader(
            name = animalData?.name ?: "Ładowanie...",
            location = animalData?.location ?: "..."
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimalTabsBar(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { newIndex -> selectedTabIndex = newIndex }
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
        ) {
            if (animalData == null) {
                Text(
                    text = "Pobieranie danych z bazy...",
                    modifier = Modifier.align(Alignment.Center),
                    color = AnimonGreen
                )
            } else {
                when (selectedTabIndex) {
                    0 -> BasicInfoContent(animal = animalData!!)
                    1 -> MedicalInfoContent(records = medicalRecords)
                    2 -> PassportContent(sections = passportSections)
                }
            }
        }
    }
}

@Composable
fun AnimalImage() {
    Box(
        modifier = Modifier
            .size(116.dp)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .border(width = 3.dp, color = AnimonGreen, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog),
            contentDescription = "Zdjęcie zwierzęcia",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun AnimalHeader(
    name: String,
    location: String
) {
    Text(
        text = name,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        letterSpacing = 0.5.sp,
        modifier = Modifier
            .padding(top = 20.dp)
    )

    Spacer(modifier = Modifier.size(12.dp))

    Row(
        modifier = Modifier
            .background(
                color = AnimonGreen.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Lokalizacja zwierzęcia",
            tint = AnimonGreen,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = location,
            fontSize = 14.sp,
            color = AnimonGreen,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AnimalTabsBar(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = AnimonGreen,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = AnimonGreen
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selectedTabIndex == index) AnimonGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}

@Composable
fun InfoTile(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
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
            text = value,
            fontSize = 18.sp,
            color = AnimonTileBeige,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BasicInfoContent(animal: AnimalData) {
    val infoItems = listOf(
        "Waga" to animal.weight,
        "Wiek" to animal.age,
        "Płeć" to animal.gender,
        "Gatunek" to animal.species,
        "Wielkość" to animal.size,
        "Kastracja" to animal.castration
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(infoItems.size) { index ->
            val (label, value) = infoItems[index]
            InfoTile(
                label = label,
                value = value
            )
        }
    }
}

@Composable
fun MedicalInfoTile(
    title: String,
    date: String,
    description: String
) {
    var isExpanded by remember { mutableStateOf(false) }

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
                color = AnimonTileBeige.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 8.dp)
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
            }
        }
    }
}

@Composable
fun MedicalInfoContent(records: List<MedicalRecord>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (records.isEmpty()) {
            Text(
                text = "Brak wpisów medycznych dla tego zwierzęcia.",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            records.forEach { record ->
                MedicalInfoTile(
                    title = record.title,
                    date = record.date,
                    description = record.description
                )
            }
        }
    }
}

@Composable
fun PassportSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AnimonTileGreen
        )
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
                        text = subtitle,
                        fontSize = 11.sp,
                        color = AnimonTileBeige.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = AnimonTileBeige,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
                    tint = AnimonTileBeige
                )
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
                }
            }
        }
    }
}

@Composable
fun PassportRowItem(label: String, value: String) {
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
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = AnimonTileBeige,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PassportContent(sections: List<PassportSection>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (sections.isEmpty()) {
            Text(
                text = "Brak danych paszportowych dla tego zwierzęcia.",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        } else {
            sections.forEach { section ->
                PassportSection(
                    title = section.title,
                    subtitle = section.subtitle
                ) {
                    section.items.forEach { (label, value) ->
                        PassportRowItem(label = label, value = value)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}