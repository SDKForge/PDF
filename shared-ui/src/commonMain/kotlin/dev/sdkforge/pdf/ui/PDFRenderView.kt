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

/**
 * A composable that renders all pages of a PDF document in a scrollable list.
 *
 * This composable displays all pages of a PDF document vertically in a scrollable column.
 * Each page is rendered using [PDFRenderPageView] with proper spacing and layout.
 *
 * ## Features
 *
 * - **Lazy loading**: Pages are rendered on-demand as they become visible
 * - **Responsive layout**: Pages scale to fit the available width
 * - **Loading states**: Shows progress indicators while pages are being rendered
 * - **Memory efficient**: Uses Compose's remember with key for proper page caching
 *
 * ## Usage
 *
 * ```kotlin
 * PDFRenderView(
 *     document = pdfDocument,
 *     modifier = Modifier.fillMaxSize()
 * )
 * ```
 *
 * @param document The PDF document to render
 * @param modifier Optional modifier to apply to the composable
 */
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

/**
 * A composable that renders a single PDF page.
 *
 * This composable displays a single PDF page with proper scaling and layout.
 * It handles both loading states (showing a progress indicator) and rendered states
 * (showing the actual page content).
 *
 * ## Features
 *
 * - **Loading state**: Shows a circular progress indicator while the page is being rendered
 * - **Content scaling**: Pages are scaled to fill the available width while maintaining aspect ratio
 * - **Visual feedback**: Red border around the page area for debugging/visual clarity
 * - **Background**: White background to ensure proper contrast
 *
 * ## Usage
 *
 * ```kotlin
 * PDFRenderPageView(
 *     page = pdfPage,
 *     modifier = Modifier.fillMaxWidth()
 * )
 * ```
 *
 * @param page The PDF page to render, or `null` if the page is still loading
 * @param modifier Optional modifier to apply to the composable
 */
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
