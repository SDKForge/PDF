@file:Suppress("ktlint:standard:class-signature", "ktlint:standard:function-signature", "ktlint:standard:function-expression-body")

package dev.sdkforge.pdf.ui

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor

actual class PDFDocument(
    private val pdfRenderer: PdfRenderer,
) {
    private val pages: List<PDFPage> = buildList {
        for (index in 0..<pdfRenderer.pageCount) {
            this += PDFPage(
                page = pdfRenderer.openPage(index),
            )
        }
    }

    constructor(
        context: Context,
        fileName: String,
    ) : this(
        pdfRenderer = PdfRenderer(
            context.assets.openFd(fileName).parcelFileDescriptor,
        ),
    )

    constructor(
        parcelFileDescriptor: ParcelFileDescriptor,
    ) : this(
        pdfRenderer = PdfRenderer(
            parcelFileDescriptor,
        ),
    )

    actual val pageCount: Int
        get() = pdfRenderer.pageCount

    actual fun getPageAt(index: Int): PDFPage? {
        return pages.getOrNull(index)
    }
}
