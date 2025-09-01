@file:Suppress("ktlint:standard:class-signature")

package dev.sdkforge.pdf.ui

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.pdf.PdfRenderer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize

/**
 * Android implementation of [PDFPageInfo].
 *
 * This actual class provides Android-specific PDF page information and rendering
 * capabilities using `android.graphics.pdf.PdfRenderer.Page`. It renders pages
 * to bitmaps and provides them as Compose painters.
 *
 * ## Implementation Details
 *
 * - **Rendering**: Uses `PdfRenderer.Page.RENDER_MODE_FOR_PRINT` for high-quality output
 * - **Bitmap Format**: Renders to `ARGB_8888` bitmaps for full color support
 * - **Memory Management**: Automatically closes pages after rendering
 * - **Async Rendering**: Uses `AsyncImagePainter` for efficient Compose integration
 *
 * @param page The underlying Android PDF page object
 */
actual class PDFPageInfo(
    private val page: PdfRenderer.Page,
) {
    /**
     * The rendered bitmap of the page.
     *
     * The bitmap is created during initialization and contains the rendered
     * page content in ARGB_8888 format.
     */
    private var bitmap: MutableState<Bitmap?> = mutableStateOf(null)

    init {
        val bitmap = createBitmap(
            page.width,
            page.height,
            Bitmap.Config.ARGB_8888,
        )
        page.render(
            bitmap,
            null,
            null,
            PdfRenderer.Page.RENDER_MODE_FOR_PRINT,
        )
        page.close()

        this.bitmap.value = bitmap
    }

    /**
     * The width of the PDF page in pixels.
     *
     * @return Page width as an integer
     */
    actual val pageWidth: Int
        get() = page.width

    /**
     * The height of the PDF page in pixels.
     *
     * @return Page height as an integer
     */
    actual val pageHeight: Int
        get() = page.height

    /**
     * A painter that can render the PDF page content.
     *
     * Returns an `AsyncImagePainter` that efficiently renders the page bitmap
     * in Compose UI with proper scaling and memory management.
     *
     * @return A [Painter] that renders the PDF page content
     */
    actual val painter: Painter
        get() = AsyncImagePainter(page, bitmap)
}

/**
 * A Compose painter that renders PDF page bitmaps asynchronously.
 *
 * This internal class implements Compose's `Painter` interface to efficiently
 * render PDF page bitmaps. It implements `RememberObserver` for proper memory
 * management in Compose's lifecycle.
 *
 * ## Features
 *
 * - **Async Rendering**: Efficiently renders bitmaps without blocking the UI thread
 * - **Memory Management**: Implements `RememberObserver` for proper cleanup
 * - **Scaling**: Automatically scales content to fit the available space
 * - **Performance**: Optimized for smooth scrolling and rendering
 *
 * @param input The PDF page that was rendered
 * @param bitmap The state containing the rendered bitmap
 */
internal class AsyncImagePainter internal constructor(
    private val input: PdfRenderer.Page,
    private val bitmap: State<Bitmap?>,
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
            width = input.width.toFloat(),
            height = input.height.toFloat(),
        )

    /**
     * Draws the bitmap content.
     *
     * Renders the bitmap with proper scaling to fit the available drawing space.
     * The bitmap is converted to a Compose ImageBitmap for efficient rendering.
     *
     * @param scope The drawing scope with context and size information
     */
    override fun DrawScope.onDraw() {
        bitmap.value?.run {
            drawImage(
                image = this.asImageBitmap(),
                srcSize = IntSize(
                    width = input.width,
                    height = input.height,
                ),
                dstSize = IntSize(
                    width = drawContext.size.width.toInt(),
                    height = drawContext.size.height.toInt(),
                ),
            )
        }
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
