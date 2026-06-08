package com.example.animon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.core.designsystem.AnimonTheme
import com.example.animon.core.util.rememberIsNetworkAvailable
import com.example.animon.feature.auth.ui.LoginScreen
import com.example.animon.feature.details.ui.AnimalDetailsScreen
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.animon.feature.home.ui.HomeScreen
import com.example.animon.feature.notifications.ui.NotificationsScreen
import com.example.animon.feature.notifications.viewmodel.NotificationsViewModel
import com.example.animon.feature.offline.OfflineBanner
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
    val isNetworkAvailable by rememberIsNetworkAvailable()

    val internalNavController = rememberNavController()

    val notificationsViewModel: NotificationsViewModel = viewModel()

    val notifications by notificationsViewModel.notifications.collectAsState()
    val notificationCount = notifications.count { !it.isRead }

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

                    IconButton(onClick = {
                            internalNavController.navigate("notifications") {
                            popUpTo(internalNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
                        BadgedBox(
                            modifier = Modifier.padding(8.dp),
                            badge = {
                                if (notificationCount > 0) {
                                    Badge(
                                        containerColor = Color.Red,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = notificationCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Powiadomienia",
                                modifier = Modifier.size(30.dp)
                            )
                        }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            NavHost(
                navController = internalNavController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = "home") {
                    HomeScreen(
                        navController = internalNavController,
                        onAnimalClick = { animalId ->
                            internalNavController.navigate("details/$animalId")
                        }
                    )
                }

                composable(
                    route = "details/{animalId}",
                    arguments = listOf(
                        navArgument("animalId") { type = NavType.StringType }
                    )) {
                    AnimalDetailsScreen(navController = internalNavController)
                }

                composable(route = "my_profile") {
                    ProfileScreen(navController = internalNavController, onLogout = {
                        rootNavController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    })
                }

                composable(
                    route = "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) {
                    ProfileScreen(navController = internalNavController, onLogout = {
                        rootNavController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    })
                }

                composable(route = "notifications") {
                    NotificationsScreen(
                        viewModel = notificationsViewModel,
                        navController = internalNavController,
                        onAnimalClick = { animalId ->
                            internalNavController.navigate("details/$animalId")
                        }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(10f)
                    .statusBarsPadding()
                    .padding(top = 70.dp)
            ) {
                OfflineBanner(isOffline = !isNetworkAvailable)
            }
        }
    }
}