package dev.sdkforge.pdf.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sdkforge.pdf.ui.PDFDocument
import dev.sdkforge.pdf.ui.PDFRenderView

@Composable
fun App(
    document: PDFDocument,
    modifier: Modifier = Modifier,
) = ApplicationTheme {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PDFRenderView(
                document = document,
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}
