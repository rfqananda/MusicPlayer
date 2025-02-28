package com.example.uicomponent.error_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.uicomponent.R

@Composable
fun ErrorView(modifier: Modifier = Modifier, onRetry: () -> Unit = {}) {
    val context = LocalContext.current
    val color = Color(ContextCompat.getColor(context, R.color.bluePrimary))

    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(id = R.drawable.error),
            contentDescription = "search image",
            modifier = Modifier
                .size(200.dp).align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Terjadi kesalahan...",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 16.sp,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            onClick = onRetry,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = Color.White
            )
        ) {
            Text(text = "Coba Lagi", fontSize = 14.sp)
        }
    }
}