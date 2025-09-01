@file:Suppress("ktlint:standard:class-signature")

package dev.sdkforge.pdf.ui

import platform.PDFKit.PDFPage

/**
 * iOS implementation of [PDFPage].
 *
 * This actual class wraps the iOS `PDFKit.PDFPage` and provides access to
 * page information through the common interface. The page is automatically
 * converted to a [PDFPageInfo] for rendering.
 *
 * ## Implementation Details
 *
 * - **Page Source**: Uses `platform.PDFKit.PDFPage`
 * - **Rendering**: Pages are rendered to UIImage using Core Graphics
 * - **Memory Management**: Efficient memory usage with proper cleanup
 * - **Performance**: Optimized for iOS PDF rendering pipeline
 *
 * @param page The underlying iOS PDF page object
 */
actual class PDFPage(
    internal val page: PDFPage,
) {
    /**
     * Information about this PDF page.
     *
     * Creates a [PDFPageInfo] instance that wraps the iOS page and provides
     * rendering capabilities through Compose painters.
     *
     * @return [PDFPageInfo] containing page dimensions and rendering capabilities
     */
    actual val pageInfo: PDFPageInfo = PDFPageInfo(page)
}
