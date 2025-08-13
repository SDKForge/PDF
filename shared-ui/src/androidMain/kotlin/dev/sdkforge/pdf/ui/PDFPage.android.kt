package dev.sdkforge.pdf.ui

import android.graphics.pdf.PdfRenderer

actual class PDFPage(internal val page: PdfRenderer.Page) {
    actual val pageInfo: PDFPageInfo = PDFPageInfo(page)
}
