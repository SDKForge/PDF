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

/**
 * iOS implementation of [PDFPageInfo].
 *
 * This actual class provides iOS-specific PDF page information and rendering
 * capabilities using `platform.PDFKit.PDFPage` and Core Graphics. It renders
 * pages to UIImage and provides them as Compose painters.
 *
 * ## Implementation Details
 *
 * - **Rendering**: Uses Core Graphics to render PDF pages to UIImage
 * - **Coordinate System**: Handles iOS coordinate system transformations
 * - **Memory Management**: Efficient memory usage with proper cleanup
 * - **Async Rendering**: Uses `AsyncImagePainter` for efficient Compose integration
 *
 * ## Rendering Process
 *
 * 1. **Page Reference**: Gets the underlying PDF page reference
 * 2. **Context Setup**: Creates a graphics context with proper dimensions
 * 3. **Transformations**: Applies coordinate system transformations
 * 4. **Rendering**: Draws the PDF page using Core Graphics
 * 5. **Image Extraction**: Extracts the rendered UIImage
 *
 * @param pdfPage The underlying iOS PDF page object
 */
@OptIn(ExperimentalForeignApi::class)
actual class PDFPageInfo(
    private val pdfPage: PDFPage,
) {
    /**
     * The rendered UIImage of the page.
     *
     * The UIImage is created during initialization and contains the rendered
     * page content in PNG format.
     */
    private var uiImage: MutableState<UIImage?> = mutableStateOf(null)

    /**
     * The width of the PDF page in pixels.
     *
     * @return Page width as an integer
     */
    actual val pageWidth: Int
        get() = pdfPage.boundsForBox(kPDFDisplayBoxMediaBox)
            .useContents { this.size.width.toInt() }

    /**
     * The height of the PDF page in pixels.
     *
     * @return Page height as an integer
     */
    actual val pageHeight: Int
        get() = pdfPage.boundsForBox(kPDFDisplayBoxMediaBox)
            .useContents { this.size.height.toInt() }

    /**
     * A painter that can render the PDF page content.
     *
     * Returns an `AsyncImagePainter` that efficiently renders the page UIImage
     * in Compose UI with proper scaling and memory management.
     *
     * @return A [Painter] that renders the PDF page content
     */
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

/**
 * A Compose painter that renders PDF page UIImages asynchronously.
 *
 * This internal class implements Compose's `Painter` interface to efficiently
 * render PDF page UIImages. It implements `RememberObserver` for proper memory
 * management in Compose's lifecycle.
 *
 * ## Features
 *
 * - **Async Rendering**: Efficiently renders UIImages without blocking the UI thread
 * - **Memory Management**: Implements `RememberObserver` for proper cleanup
 * - **Scaling**: Automatically scales content to fit the available space
 * - **Performance**: Optimized for smooth scrolling and rendering
 * - **Format Conversion**: Converts UIImage to Compose ImageBitmap for rendering
 *
 * @param input The PDF page that was rendered
 * @param pageWidth The width of the page in pixels
 * @param pageHeight The height of the page in pixels
 * @param image The state containing the rendered UIImage
 */
internal class AsyncImagePainter internal constructor(
    private val input: PDFPage,
    private val pageWidth: Int,
    private val pageHeight: Int,
    private val image: State<UIImage?>,
) : Painter(), RememberObserver {

    /**
     * The intrinsic size of the painter.
     *
     * Returns the original dimensions of the PDF page as a Compose Size.
     *
     * @return The original page dimensions
     */
    override val intrinsicSize: Size
        get() = Size(
            width = pageWidth.toFloat(),
            height = pageHeight.toFloat(),
        )

    /**
     * Draws the UIImage content.
     *
     * Renders the UIImage with proper scaling to fit the available drawing space.
     * The UIImage is converted to a Compose ImageBitmap for efficient rendering.
     *
     * @param scope The drawing scope with context and size information
     */
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

    /**
     * Called when the painter is abandoned.
     *
     * No cleanup needed for this implementation.
     */
    override fun onAbandoned() = Unit

    /**
     * Called when the painter is forgotten.
     *
     * No cleanup needed for this implementation.
     */
    override fun onForgotten() = Unit

    /**
     * Called when the painter is remembered.
     *
     * No initialization needed for this implementation.
     */
    override fun onRemembered() = Unit
}

/**
 * Extension function to convert NSData to ByteArray.
 *
 * This utility function converts iOS NSData to a Kotlin ByteArray for use
 * with Skia image processing. It uses pinned memory for efficient data transfer.
 *
 * @return A ByteArray containing the NSData bytes
 */
@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
    }
}
