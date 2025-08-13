@file:Suppress("ktlint:standard:class-signature", "ktlint:standard:function-signature", "ktlint:standard:function-expression-body")

package dev.sdkforge.pdf.ui

import platform.Foundation.NSBundle
import platform.PDFKit.PDFDocument

actual class PDFDocument(
    private val pdfDocument: PDFDocument,
) {
    constructor(fileName: String) : this(
        pdfDocument = PDFDocument(
            uRL = NSBundle.mainBundle.URLForResource(fileName, "pdf")!!,
        ),
    )

    actual val pageCount: Int
        get() = pdfDocument.pageCount.toInt()

    actual fun getPageAt(index: Int): PDFPage? {
        return pdfDocument.pageAtIndex(index.toULong())?.let {
            PDFPage(it)
        }
    }
}
