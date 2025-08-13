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

actual class PDFPageInfo(
    private val page: PdfRenderer.Page,
) {
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

    actual val pageWidth: Int
        get() = page.width

    actual val pageHeight: Int
        get() = page.height

    actual val painter: Painter
        get() = AsyncImagePainter(page, bitmap)
}

internal class AsyncImagePainter internal constructor(
    private val input: PdfRenderer.Page,
    private val bitmap: State<Bitmap?>,
) : Painter(), RememberObserver {

    override val intrinsicSize: Size
        get() = Size(
            width = input.width.toFloat(),
            height = input.height.toFloat(),
        )

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

    override fun onAbandoned() = Unit
    override fun onForgotten() = Unit
    override fun onRemembered() = Unit
}
