@file:Suppress("ktlint:standard:class-signature")

package dev.sdkforge.pdf.ui

import platform.PDFKit.PDFPage

actual class PDFPage(
    internal val page: PDFPage,
) {
    actual val pageInfo: PDFPageInfo = PDFPageInfo(page)
}
