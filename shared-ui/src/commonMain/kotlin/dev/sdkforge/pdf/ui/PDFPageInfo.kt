package dev.sdkforge.pdf.ui

import androidx.compose.ui.graphics.painter.Painter

/**
 * Information about a PDF page including its dimensions and rendering capabilities.
 *
 * This expect class provides metadata about a PDF page and a painter that can be used
 * to render the page content in Compose UI. The actual implementation handles the
 * platform-specific PDF rendering:
 * - **Android**: Uses `android.graphics.pdf.PdfRenderer` to render to bitmap
 * - **iOS**: Uses `platform.PDFKit` and Core Graphics to render to UIImage
 *
 * ## Usage
 *
 * ```kotlin
 * val pageInfo = page.pageInfo
 * val width = pageInfo.pageWidth
 * val height = pageInfo.pageHeight
 * val painter = pageInfo.painter
 *
 * Image(
 *     painter = painter,
 *     contentDescription = "PDF Page",
 *     modifier = Modifier.size(width.dp, height.dp)
 * )
 * ```
 *
 * @property pageWidth The width of the page in pixels
 * @property pageHeight The height of the page in pixels
 * @property painter A Compose painter that can render the page content
 */
expect class PDFPageInfo {
    /**
     * The width of the PDF page in pixels.
     *
     * @return Page width as an integer
     */
    val pageWidth: Int

    /**
     * The height of the PDF page in pixels.
     *
     * @return Page height as an integer
     */
    val pageHeight: Int

    /**
     * A painter that can render the PDF page content.
     *
     * This painter can be used with Compose's `Image` composable to display
     * the PDF page content. The painter handles the platform-specific rendering
     * internally.
     *
     * @return A [Painter] that renders the PDF page content
     */
    val painter: Painter
}
