package com.example.animon.feature.offline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OfflineBanner(isOffline: Boolean) {
    AnimatedVisibility(
        visible = isOffline,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(50.dp))
                    .background(Color(0xFFFFB300))
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SignalWifiOff,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "TRYB OFFLINE - brak synchronizacji",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}