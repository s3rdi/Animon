package com.example.animon.feature.details.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.animon.R
import com.example.animon.core.designsystem.AnimonGreen

@Composable
fun AnimalDetailsScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dane podstawowe", "Dane medyczne", "Paszport")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimalImage()

        AnimalHeader()

        Spacer(modifier = Modifier.height(24.dp))

        AnimalTabsBar(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { newIndex -> selectedTabIndex = newIndex }
        )

//        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//            when (selectedTabIndex) {
//                0 -> BasicInfoContent()   // Tutaj stwórz Composable z danymi podstawowymi
//                1 -> MedicalInfoContent() // Tutaj stwórz Composable z danymi medycznymi
//                2 -> PassportContent()    // Tutaj stwórz Composable z paszportem
//            }
//        }
    }
}

@Composable
fun AnimalImage() {
    Box(
        modifier = Modifier
            .size(146.dp)
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
                .size(140.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun AnimalHeader() {
    Text(
        text = "Imię zwierzaka",
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
            text = "Sektor F",
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