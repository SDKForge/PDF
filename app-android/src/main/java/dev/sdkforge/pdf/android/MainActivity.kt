package dev.sdkforge.pdf.android

import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dev.sdkforge.pdf.app.App
import dev.sdkforge.pdf.ui.PDFDocument
import java.io.File
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)

        runBlocking(NonCancellable) {
            assets.open("dictionary.pdf").run {
                copyTo(File(cacheDir, "dictionary.pdf").outputStream())
            }
        }

        enableEdgeToEdge()
        setContent {
            App(
                document = PDFDocument(
                    ParcelFileDescriptor.open(
                        File(cacheDir, "dictionary.pdf"),
                        ParcelFileDescriptor.MODE_READ_ONLY,
                    ),
                ),
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}
