package com.example.animon.feature.details.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.navigation.NavController
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.feature.details.viewmodel.AnimalDetailsViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailsScreen(
    viewModel: AnimalDetailsViewModel = viewModel(),
    navController: NavController
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dane podstawowe", "Dane medyczne", "Paszport")

    val animalData by viewModel.animalState.collectAsState()
    val medicalRecords by viewModel.medicalRecordsState.collectAsState()
    val passportSections by viewModel.passportState.collectAsState()

    val calculatedStatus by viewModel.calculatedStatusState.collectAsState()

    val isVet by viewModel.isVeterinarian.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Szczegóły")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "cofnij",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AnimonGreen,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimalImage(animalData?.photo ?: "")

            AnimalHeader(
                name = animalData?.name ?: "Ładowanie...",
                location = animalData?.location ?: "...",
                onNameChanged = { newName ->
                    animalData?.let { currentAnimal ->
                        val updatedAnimal = currentAnimal.copy(name = newName)
                        viewModel.updateAnimalDocument(updatedAnimal)
                    }
                },
                onLocationClick = {
                    animalData?.location.let { loc ->
                        navController.previousBackStackEntry?.savedStateHandle?.set("filter_sector", loc)
                        navController.popBackStack()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimalTabsBar(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { newIndex -> selectedTabIndex = newIndex }
            )

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
            ) {
                if (animalData == null) {
                    Text(
                        text = "Pobieranie danych z bazy...",
                        modifier = Modifier.align(Alignment.Center),
                        color = AnimonGreen
                    )
                } else {
                    when (selectedTabIndex) {
                        0 -> BasicInfoContent(
                            animal = animalData!!,
                            status = calculatedStatus,
                            viewModel = viewModel,
                            onUpdate = { updatedAnimal ->
                                viewModel.updateAnimalDocument(updatedAnimal)
                            })

                        1 -> MedicalInfoContent(
                            records = medicalRecords,
                            viewModel = viewModel,
                            navController = navController,
                            isVet = isVet,
                            onAddNewRecord = { title, desc, date ->
                                viewModel.addMedicalRecord(title, desc, date)
                            }
                        )

                        2 -> PassportContent(
                            sections = passportSections,
                            isVet = isVet,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("LocalContextResourcesRead", "DiscouragedApi")
@Composable
fun AnimalImage(imageName: String) {
    val context = LocalContext.current
    val imageResId = remember(imageName) {
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }

    Box(
        modifier = Modifier
            .size(106.dp)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .border(width = 3.dp, color = AnimonGreen, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Zdjęcie zwierzęcia",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Pets,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
            }
        }
    }
}

@Composable
fun AnimalHeader(
    name: String,
    location: String,
    onNameChanged: (String) -> Unit,
    onLocationClick: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editableName by remember(name) { mutableStateOf(name) }

    if (isEditing) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 20.dp)
        ) {
            OutlinedTextField(
                value = editableName,
                onValueChange = { editableName = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = {
                if (editableName.isNotBlank()) {
                    onNameChanged(editableName)
                    isEditing = false
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Zapisz imię",
                    tint = AnimonGreen
                )
            }
        }
    } else {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .clickable { isEditing = true }
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .clickable { onLocationClick() }
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
            modifier = Modifier.size(14.dp)
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