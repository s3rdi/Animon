package com.example.animon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.core.designsystem.AnimonTheme
import com.example.animon.feature.auth.ui.LoginScreen
import com.example.animon.feature.details.ui.AnimalDetailsScreen
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.animon.feature.home.ui.HomeScreen
import com.example.animon.feature.profile.ui.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimonTheme {
                val rootNavController = rememberNavController()

                NavHost(
                    navController = rootNavController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                rootNavController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        MainAppContainer(rootNavController = rootNavController)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(rootNavController: NavHostController) {
    val internalNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = AnimonGreen,
                contentColor = AnimonBeige,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = {
                        internalNavController.navigate("home") {
                            popUpTo(internalNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = "Lista zwierząt",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
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

                    IconButton(onClick = {
                        internalNavController.navigate("my_profile") {
                            popUpTo(internalNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
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
        NavHost(
            navController = internalNavController,
            startDestination = "home",
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(route = "home") {
                HomeScreen(
                    onAnimalClick = { animalId ->
                        internalNavController.navigate("details/$animalId")
                    }
                )
            }

            composable(route = "details/{animalId}",
                arguments = listOf(
                    navArgument("animalId") { type = NavType.StringType }
                )) {
                AnimalDetailsScreen(navController = internalNavController)
            }

            composable(route = "my_profile") {
                ProfileScreen(onLogout = {
                    rootNavController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                })
            }

            composable(route = "profile/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                ProfileScreen(onLogout = {
                    rootNavController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                })
            }
        }
    }
}