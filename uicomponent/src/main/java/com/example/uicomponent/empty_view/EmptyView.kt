package com.example.uicomponent.empty_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uicomponent.R


@Composable
fun EmptyView(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(id = R.drawable.not_found),
            contentDescription = "search image",
            modifier = Modifier
                .size(200.dp).align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Lagu yang dicari tidak ditemukan",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 16.sp,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge
        )
    }
}