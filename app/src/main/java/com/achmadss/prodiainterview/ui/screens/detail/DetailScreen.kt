package com.achmadss.prodiainterview.ui.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil.compose.SubcomposeAsyncImage
import com.achmadss.prodiainterview.R
import com.achmadss.prodiainterview.ui.common.formatDateTime
import com.achmadss.prodiainterview.ui.common.rememberResourceBitmapPainter

data class DetailScreen(
    val imageUrl: String,
    val title: String,
    val publishedAt: String,
    val summary: String,
) : Screen {

    @Composable
    override fun Content() {
        val summaryText = summary.split(".").firstOrNull()
        val publishedAtFormatted = publishedAt.formatDateTime()
        Surface {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = imageUrl,
                    contentDescription = "",
                    loading = {
                        Box (
                            modifier = Modifier.padding(32.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    },
                    error = {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                            painter = rememberResourceBitmapPainter(id = R.drawable.cover_error),
                            contentDescription = ""
                        )
                    }
                )
                Text(
                    modifier = Modifier
                        .padding(16.dp),
                    text = publishedAtFormatted,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = summaryText?.plus(".") ?: "No Summary",
                    style = MaterialTheme.typography.titleLarge
                )

            }
        }
    }

}