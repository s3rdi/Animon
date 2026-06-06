package com.example.animon.feature.profile.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.core.designsystem.AnimonTileBeige
import com.example.animon.core.designsystem.AnimonTileGreen
import com.example.animon.feature.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val profileData by viewModel.profileState.collectAsState()
    val isOwnProfile by viewModel.isOwnProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AnimonGreen,
                    titleContentColor = AnimonTileBeige
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(top = 32.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(width = 3.dp, color = AnimonGreen, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Zdjęcie profilowe",
                    modifier = Modifier.size(110.dp),
                    tint = AnimonGreen
                )
            }

            if (profileData == null) {
                Spacer(modifier = Modifier.height(32.dp))
                Text("Pobieranie danych pracownika...", color = AnimonGreen)
            } else {
                val user = profileData!!

                Text(
                    text = "${user.first_name} ${user.second_name}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = user.position,
                    fontSize = 16.sp,
                    color = AnimonGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileInfoRow(label = "Email służbowy", value = user.email)
                    ProfileInfoRow(label = "Numer telefonu", value = user.phone_number)
                    ProfileInfoRow(label = "Sektor przypisany", value = user.sector)
                    ProfileInfoRow(label = "Data zatrudnienia", value = user.date_of_employment)
                    ProfileInfoRow(
                        label = "Dostęp do zwierząt",
                        value = user.dangerous_animals_clearance
                    )

                    ProfileSkillsRow(label = "Kwalifikacje i umiejętności", skills = user.skills)
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(visible = isOwnProfile) {
                    Button(
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(55.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "WYLOGUJ SIĘ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AnimonTileGreen, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = AnimonTileBeige.copy(alpha = 0.8f))
        Text(text = value, fontSize = 14.sp, color = AnimonTileBeige, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ProfileSkillsRow(label: String, skills: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AnimonTileGreen, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = AnimonTileBeige.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (skills.isEmpty()) {
            Text(text = "Brak wpisanych kwalifikacji", fontSize = 14.sp, color = AnimonTileBeige)
        } else {
            skills.forEach { skill ->
                Text(
                    text = "• $skill",
                    fontSize = 14.sp,
                    color = AnimonTileBeige,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}