package dev.sdkforge.pdf.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import dev.sdkforge.pdf.ui.PDFDocument
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@Suppress("FunctionName", "unused")
@ObjCName("create")
fun ComposeAppViewController() = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
    },
) {
    App(
        document = PDFDocument(
            fileName = "dictionary",
        ),
        modifier = Modifier
            .fillMaxSize(),
    )
}
