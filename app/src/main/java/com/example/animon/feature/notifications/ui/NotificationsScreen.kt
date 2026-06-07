package com.example.animon.feature.notifications.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.feature.notifications.viewmodel.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = viewModel(),
    navController: NavController,
    onAnimalClick: (String) -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Powiadomienia")
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    Text(
                        text = "Brak nowych powiadomień",
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(notifications) { notification ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAnimalClick(notification.animalId) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = notification.title,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = notification.message,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (notification.formattedDate.isNotEmpty()) {
                                Text(
                                    text = notification.formattedDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.deleteNotification(notification.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Usuń powiadomienie",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}