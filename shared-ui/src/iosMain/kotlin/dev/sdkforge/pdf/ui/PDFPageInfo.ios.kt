@file:Suppress("ktlint:standard:class-signature")

package dev.sdkforge.pdf.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGContextConcatCTM
import platform.CoreGraphics.CGContextDrawPDFPage
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGPDFPageGetBoxRect
import platform.CoreGraphics.CGPDFPageGetDrawingTransform
import platform.CoreGraphics.CGSizeMake
import platform.CoreGraphics.kCGPDFMediaBox
import platform.Foundation.NSData
import platform.PDFKit.PDFPage
import platform.PDFKit.kPDFDisplayBoxMediaBox
import platform.UIKit.UIGraphicsBeginImageContext
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual class PDFPageInfo(
    private val pdfPage: PDFPage,
) {
    private var uiImage: MutableState<UIImage?> = mutableStateOf(null)

    actual val pageWidth: Int
        get() = pdfPage.boundsForBox(kPDFDisplayBoxMediaBox)
            .useContents { this.size.width.toInt() }

    actual val pageHeight: Int
        get() = pdfPage.boundsForBox(kPDFDisplayBoxMediaBox)
            .useContents { this.size.height.toInt() }

    actual val painter: Painter
        get() = AsyncImagePainter(
            pdfPage,
            pageWidth,
            pageHeight,
            uiImage,
        )

    init {
        val pageRef = pdfPage.pageRef!!
        val pageRect = CGPDFPageGetBoxRect(
            page = pageRef,
            box = kCGPDFMediaBox,
        )

        val pageRectSize = CGSizeMake(
            width = pageRect.useContents { this.size.width },
            height = pageRect.useContents { this.size.height },
        )

        UIGraphicsBeginImageContext(pageRectSize)

        val context: CGContextRef = UIGraphicsGetCurrentContext()!!

        CGContextSaveGState(
            c = context,
        )

        CGContextTranslateCTM(
            c = context,
            tx = 0.0,
            ty = pageRect.useContents { this.size.height },
        )
        CGContextScaleCTM(
            c = context,
            sx = 1.0,
            sy = -1.0,
        )
        CGContextConcatCTM(
            c = context,
            transform = CGPDFPageGetDrawingTransform(
                page = pageRef,
                box = kCGPDFMediaBox,
                rect = pageRect,
                rotate = 0,
                preserveAspectRatio = true,
            ),
        )
        CGContextDrawPDFPage(
            c = context,
            page = pageRef,
        )
        CGContextRestoreGState(
            c = context,
        )

        uiImage.value = UIGraphicsGetImageFromCurrentImageContext()!!

        UIGraphicsEndImageContext()
    }
}

internal class AsyncImagePainter internal constructor(
    private val input: PDFPage,
    private val pageWidth: Int,
    private val pageHeight: Int,
    private val image: State<UIImage?>,
) : Painter(), RememberObserver {

    override val intrinsicSize: Size
        get() = Size(
            width = pageWidth.toFloat(),
            height = pageHeight.toFloat(),
        )

    @OptIn(ExperimentalForeignApi::class)
    override fun DrawScope.onDraw() {
        val data = UIImagePNGRepresentation(
            image.value!!,
        )

        val bytes = data!!.toByteArray()

        val bitmap = Image.makeFromEncoded(bytes).toComposeImageBitmap()

        drawImage(
            image = bitmap,
            srcSize = IntSize(
                width = pageWidth,
                height = pageHeight,
            ),
            dstSize = IntSize(
                width = drawContext.size.width.toInt(),
                height = drawContext.size.height.toInt(),
            ),
        )
    }

    override fun onAbandoned() = Unit
    override fun onForgotten() = Unit
    override fun onRemembered() = Unit
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
    }
}
