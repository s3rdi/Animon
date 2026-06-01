package com.example.animon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.core.designsystem.AnimonTheme
import com.example.animon.feature.auth.ui.LoginScreen
import com.example.animon.feature.details.ui.AnimalDetailsScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimonTheme {
                // LoginScreen()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Tytuł widoku") },
                            colors = topAppBarColors(
                                containerColor = AnimonGreen,
                                titleContentColor = AnimonBeige,
                            )
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            containerColor = AnimonGreen,
                            contentColor = AnimonBeige,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                IconButton(onClick = {  }) {
                                    Icon(
                                        imageVector = Icons.Default.Pets,
                                        contentDescription = "Lista zwierząt",
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                IconButton(onClick = {  }) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "Lista zadań",
                                        modifier = Modifier.size(50.dp)
                                    )
                                }

                                IconButton(onClick = {  }) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Powiadomienia",
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                IconButton(onClick = {  }) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Konto",
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    AnimalDetailsScreen(
                        padding = innerPadding
                    )
                }
            }
        }
    }
}