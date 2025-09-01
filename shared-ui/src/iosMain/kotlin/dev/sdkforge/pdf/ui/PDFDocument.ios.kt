@file:Suppress("ktlint:standard:class-signature", "ktlint:standard:function-signature", "ktlint:standard:function-expression-body")

package dev.sdkforge.pdf.ui

import platform.Foundation.NSBundle
import platform.PDFKit.PDFDocument

/**
 * iOS implementation of [PDFDocument].
 *
 * This actual class provides iOS-specific PDF document functionality using
 * `platform.PDFKit.PDFDocument`. It supports loading PDFs from the app bundle
 * and provides efficient page access.
 *
 * ## Implementation Details
 *
 * - **Rendering Engine**: Uses iOS's native `PDFKit` framework
 * - **Memory Management**: Efficient memory usage with automatic cleanup
 * - **Thread Safety**: Safe to use from background threads
 * - **Performance**: Optimized for iOS PDF rendering pipeline
 *
 * ## Construction Methods
 *
 * - **From Bundle**: Load PDF from app's main bundle
 * - **From Document**: Direct construction from existing `PDFDocument`
 *
 * @param pdfDocument The underlying iOS PDF document object
 */
actual class PDFDocument(
    private val pdfDocument: PDFDocument,
) {
    /**
     * Constructs a PDF document from a file in the app's main bundle.
     *
     * This constructor loads a PDF file from the main bundle and creates
     * a document for it. The file must exist in the bundle with a `.pdf` extension.
     *
     * @param fileName The name of the PDF file in the bundle (without extension)
     */
    constructor(fileName: String) : this(
        pdfDocument = PDFDocument(
            uRL = NSBundle.mainBundle.URLForResource(fileName, "pdf")!!,
        ),
    )

    /**
     * The total number of pages in the PDF document.
     *
     * @return Number of pages as an integer
     */
    actual val pageCount: Int
        get() = pdfDocument.pageCount.toInt()

    /**
     * Retrieves a page at the specified index.
     *
     * Returns the page at the given index, or `null` if the index is out of bounds.
     * The page is wrapped in a [PDFPage] instance that provides the common interface.
     *
     * @param index The zero-based index of the page to retrieve
     * @return The [PDFPage] at the specified index, or `null` if the index is out of bounds
     */
    actual fun getPageAt(index: Int): PDFPage? {
        return pdfDocument.pageAtIndex(index.toULong())?.let {
            PDFPage(it)
        }
    }
}
