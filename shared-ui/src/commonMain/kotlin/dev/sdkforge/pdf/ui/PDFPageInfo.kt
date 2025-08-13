package dev.sdkforge.pdf.ui

import androidx.compose.ui.graphics.painter.Painter

expect class PDFPageInfo {
    val pageWidth: Int
    val pageHeight: Int

    val painter: Painter
}
