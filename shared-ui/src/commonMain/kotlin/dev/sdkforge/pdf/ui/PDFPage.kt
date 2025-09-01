package dev.sdkforge.pdf.ui

/**
 * A single page from a PDF document.
 *
 * This expect class represents a PDF page and provides access to its metadata
 * and rendering information. The actual implementation varies by platform:
 * - **Android**: Uses `android.graphics.pdf.PdfRenderer.Page`
 * - **iOS**: Uses `platform.PDFKit.PDFPage`
 *
 * ## Usage
 *
 * ```kotlin
 * val page = document.getPageAt(0)
 * val pageInfo = page.pageInfo
 * val width = pageInfo.pageWidth
 * val height = pageInfo.pageHeight
 * ```
 *
 * @property pageInfo Information about the page including dimensions and painter for rendering
 */
expect class PDFPage {
    /**
     * Information about this PDF page.
     *
     * Contains the page dimensions and a painter that can be used to render
     * the page content in Compose UI.
     *
     * @return [PDFPageInfo] containing page metadata and rendering capabilities
     */
    val pageInfo: PDFPageInfo
}
