@file:Suppress("ktlint:standard:class-signature", "ktlint:standard:function-signature", "ktlint:standard:function-expression-body")

package dev.sdkforge.pdf.ui

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor

/**
 * Android implementation of [PDFDocument].
 *
 * This actual class provides Android-specific PDF document functionality using
 * the `android.graphics.pdf.PdfRenderer`. It supports multiple construction
 * methods for different use cases.
 *
 * ## Implementation Details
 *
 * - **Rendering Engine**: Uses Android's native `PdfRenderer`
 * - **Memory Management**: Automatically manages page lifecycle
 * - **Thread Safety**: Safe to use from background threads
 * - **Performance**: Efficient page caching and rendering
 *
 * ## Construction Methods
 *
 * - **From Assets**: Load PDF from app's assets folder
 * - **From File Descriptor**: Load PDF from a `ParcelFileDescriptor`
 * - **From Renderer**: Direct construction from existing `PdfRenderer`
 *
 * @param pdfRenderer The underlying Android PDF renderer
 */
actual class PDFDocument(
    private val pdfRenderer: PdfRenderer,
) {
    /**
     * List of all pages in the document.
     *
     * Pages are pre-loaded and cached for efficient access. Each page is wrapped
     * in a [PDFPage] instance that provides the common interface.
     */
    private val pages: List<PDFPage> = buildList {
        for (index in 0..<pdfRenderer.pageCount) {
            this += PDFPage(
                page = pdfRenderer.openPage(index),
            )
        }
    }

    /**
     * Constructs a PDF document from a file in the app's assets folder.
     *
     * This constructor loads a PDF file from the assets directory and creates
     * a renderer for it. The file must exist in the assets folder.
     *
     * @param context The Android context for accessing assets
     * @param fileName The name of the PDF file in the assets folder (e.g., "sample.pdf")
     */
    constructor(
        context: Context,
        fileName: String,
    ) : this(
        pdfRenderer = PdfRenderer(
            context.assets.openFd(fileName).parcelFileDescriptor,
        ),
    )

    /**
     * Constructs a PDF document from a file descriptor.
     *
     * This constructor is useful when you have a `ParcelFileDescriptor` from
     * file operations, content providers, or other sources.
     *
     * @param parcelFileDescriptor The file descriptor containing the PDF data
     */
    constructor(
        parcelFileDescriptor: ParcelFileDescriptor,
    ) : this(
        pdfRenderer = PdfRenderer(
            parcelFileDescriptor,
        ),
    )

    /**
     * The total number of pages in the PDF document.
     *
     * @return Number of pages as an integer
     */
    actual val pageCount: Int
        get() = pdfRenderer.pageCount

    /**
     * Retrieves a page at the specified index.
     *
     * Returns the pre-loaded page at the given index, or `null` if the index
     * is out of bounds.
     *
     * @param index The zero-based index of the page to retrieve
     * @return The [PDFPage] at the specified index, or `null` if the index is out of bounds
     */
    actual fun getPageAt(index: Int): PDFPage? {
        return pages.getOrNull(index)
    }
}
