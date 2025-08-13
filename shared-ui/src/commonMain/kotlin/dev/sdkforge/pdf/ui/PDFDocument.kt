@file:Suppress("ktlint:standard:function-signature")

package dev.sdkforge.pdf.ui

expect class PDFDocument {
    val pageCount: Int

    fun getPageAt(index: Int): PDFPage?
}
