package com.example.animon.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.example.animon.core.designsystem.AnimonBeige
import com.example.animon.core.designsystem.AnimonDarkGreen
import com.example.animon.core.designsystem.AnimonGreen
import com.example.animon.feature.auth.viewmodel.LoginScreenViewModel
import com.example.animon.core.util.rememberIsNetworkAvailable
import com.example.animon.feature.offline.OfflineBanner
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isNetworkAvailable by rememberIsNetworkAvailable()

    var isLogoVisible by remember { mutableStateOf(false) }
    var isTitleVisible by remember { mutableStateOf(false) }
    var isFormVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loginEvent.collect { isSuccess ->
            if (isSuccess) {
                onLoginSuccess()
            }
        }
    }

    LaunchedEffect(Unit) {
        isLogoVisible = true
        delay(200)
        isTitleVisible = true
        delay(200)
        isFormVisible = true
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AnimonGreen)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isLogoVisible,
                enter = slideInVertically(initialOffsetY = { 30 }, animationSpec = tween(500)) +
                        fadeIn(animationSpec = tween(500))
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(AnimonBeige),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = "Logo Animon",
                        modifier = Modifier.size(90.dp),
                        tint = AnimonGreen
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = isTitleVisible,
                enter = slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ANIMON",
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimonBeige,
                        letterSpacing = 4.sp
                    )

                    Text(
                        text = "Smart Animal Care",
                        fontSize = 18.sp,
                        color = AnimonBeige,
                        letterSpacing = 1.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = isFormVisible,
                enter = slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(700)
                ) + fadeIn(animationSpec = tween(700))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        placeholder = { Text("Email...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = AnimonBeige,
                            unfocusedContainerColor = AnimonBeige,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = AnimonDarkGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        placeholder = { Text("Hasło...", color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = AnimonBeige,
                            unfocusedContainerColor = AnimonBeige,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = AnimonDarkGreen
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = uiState.errorMessage ?: "",
                        color = AnimonBeige,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(70.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = AnimonBeige)
                    } else {
                        Button(
                            onClick = { viewModel.loginWithEmailAndPassword() },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnimonBeige,
                                contentColor = AnimonDarkGreen
                            )
                        ) {
                            Text(
                                text = "ZALOGUJ SIĘ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            OfflineBanner(isOffline = !isNetworkAvailable)
        }
    }
}