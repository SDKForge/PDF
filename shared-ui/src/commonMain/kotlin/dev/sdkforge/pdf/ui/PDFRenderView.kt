package dev.sdkforge.pdf.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun PDFRenderView(
    document: PDFDocument,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            count = document.pageCount,
        ) { index ->
            val page = remember(key1 = index) { document.getPageAt(index) }

            PDFRenderPageView(
                page = page,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
fun PDFRenderPageView(
    page: PDFPage?,
    modifier: Modifier = Modifier,
) {
    val painter = page?.pageInfo?.painter

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.Red,
            ),
    ) {
        if (painter == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .requiredSize(40.dp),
            )
        } else {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize(),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}
