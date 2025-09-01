@file:Suppress("ktlint:standard:function-signature")

package dev.sdkforge.pdf.ui

/**
 * A PDF document that can be rendered and displayed.
 *
 * This expect class represents a PDF document and provides access to its pages.
 * The actual implementation varies by platform:
 * - **Android**: Uses `android.graphics.pdf.PdfRenderer`
 * - **iOS**: Uses `platform.PDFKit.PDFDocument`
 *
 * ## Usage
 *
 * ```kotlin
 * val document = PDFDocument(fileName = "sample.pdf")
 * val pageCount = document.pageCount
 * val firstPage = document.getPageAt(0)
 * ```
 *
 * @property pageCount The total number of pages in the document
 */
expect class PDFDocument {
    /**
     * The total number of pages in the PDF document.
     *
     * @return Number of pages as an integer
     */
    val pageCount: Int

    /**
     * Retrieves a page at the specified index.
     *
     * @param index The zero-based index of the page to retrieve
     * @return The [PDFPage] at the specified index, or `null` if the index is out of bounds
     */
    fun getPageAt(index: Int): PDFPage?
}
