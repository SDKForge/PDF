package dev.sdkforge.pdf.ui

import android.graphics.pdf.PdfRenderer

/**
 * Android implementation of [PDFPage].
 *
 * This actual class wraps the Android `PdfRenderer.Page` and provides access to
 * page information through the common interface. The page is automatically
 * converted to a [PDFPageInfo] for rendering.
 *
 * ## Implementation Details
 *
 * - **Page Source**: Uses `android.graphics.pdf.PdfRenderer.Page`
 * - **Rendering**: Pages are rendered to bitmaps using Android's PDF renderer
 * - **Memory Management**: Pages are automatically closed when no longer needed
 *
 * @param page The underlying Android PDF page object
 */
actual class PDFPage(internal val page: PdfRenderer.Page) {
    /**
     * Information about this PDF page.
     *
     * Creates a [PDFPageInfo] instance that wraps the Android page and provides
     * rendering capabilities through Compose painters.
     *
     * @return [PDFPageInfo] containing page dimensions and rendering capabilities
     */
    actual val pageInfo: PDFPageInfo = PDFPageInfo(page)
}
