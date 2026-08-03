package com.alpaomega1136.statsdroid.feature.reference.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceFileType
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferencePdfPage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import javax.inject.Inject

class DefaultReferenceDocumentLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ReferenceDocumentLocalDataSource {

    override fun renderPdfPage(
        material: ReferenceMaterial,
        pageIndex: Int,
    ): ReferencePdfPage {
        require(material.fileType == ReferenceFileType.PDF) {
            "Only PDF materials can be displayed in the in-app reader."
        }

        val pdfFile = resolvePdfFile(material)

        ParcelFileDescriptor.open(
            pdfFile,
            ParcelFileDescriptor.MODE_READ_ONLY,
        ).use { fileDescriptor ->
            PdfRenderer(fileDescriptor).use { renderer ->
                require(renderer.pageCount > 0) {
                    "The selected PDF does not contain any pages."
                }

                val safePageIndex = pageIndex.coerceIn(
                    minimumValue = 0,
                    maximumValue = renderer.pageCount - 1,
                )

                renderer.openPage(safePageIndex).use { page ->
                    val targetWidth = TARGET_BITMAP_WIDTH
                    val targetHeight = (
                        targetWidth.toDouble() *
                            page.height.toDouble() /
                            page.width.toDouble()
                        ).toInt().coerceAtLeast(1)

                    val bitmap = Bitmap.createBitmap(
                        targetWidth,
                        targetHeight,
                        Bitmap.Config.ARGB_8888,
                    )
                    bitmap.eraseColor(Color.WHITE)

                    page.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                    )

                    return ReferencePdfPage(
                        bitmap = bitmap,
                        pageIndex = safePageIndex,
                        pageCount = renderer.pageCount,
                    )
                }
            }
        }
    }

    private fun resolvePdfFile(
        material: ReferenceMaterial,
    ): File {
        material.assetPath?.let { assetPath ->
            return copyAssetToCache(
                assetPath = assetPath,
                materialId = material.id,
            )
        }

        material.remoteUrl?.let { remoteUrl ->
            return downloadRemotePdf(
                remoteUrl = remoteUrl,
                materialId = material.id,
            )
        }

        error("The selected material does not have a readable source.")
    }

    private fun copyAssetToCache(
        assetPath: String,
        materialId: String,
    ): File {
        val targetFile = File(
            context.cacheDir,
            "reference/bundled/${safeFileName(materialId)}.pdf",
        )
        targetFile.parentFile?.mkdirs()

        if (targetFile.exists() && targetFile.length() > 0L) {
            return targetFile
        }

        context.assets.open(assetPath).use { inputStream ->
            FileOutputStream(targetFile, false).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return targetFile
    }

    private fun downloadRemotePdf(
        remoteUrl: String,
        materialId: String,
    ): File {
        val cacheKey = sha256("$materialId|$remoteUrl")
        val targetFile = File(
            context.cacheDir,
            "reference/remote/$cacheKey.pdf",
        )
        targetFile.parentFile?.mkdirs()

        if (targetFile.exists() && targetFile.length() > 0L) {
            return targetFile
        }

        val temporaryFile = File(
            targetFile.parentFile,
            "$cacheKey.download",
        )

        val connection = URL(remoteUrl)
            .openConnection() as HttpURLConnection

        try {
            connection.connectTimeout = CONNECT_TIMEOUT_MILLIS
            connection.readTimeout = READ_TIMEOUT_MILLIS
            connection.instanceFollowRedirects = true
            connection.setRequestProperty(
                "User-Agent",
                USER_AGENT,
            )
            connection.connect()

            require(connection.responseCode in 200..299) {
                "Unable to download PDF (HTTP ${connection.responseCode})."
            }

            connection.inputStream.use { inputStream ->
                FileOutputStream(temporaryFile, false).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            require(temporaryFile.length() > 0L) {
                "The downloaded PDF is empty."
            }

            if (!temporaryFile.renameTo(targetFile)) {
                temporaryFile.copyTo(targetFile, overwrite = true)
                temporaryFile.delete()
            }

            return targetFile
        } finally {
            connection.disconnect()
            if (temporaryFile.exists() && !targetFile.exists()) {
                temporaryFile.delete()
            }
        }
    }

    private fun safeFileName(value: String): String {
        return value.replace(
            regex = Regex("[^A-Za-z0-9._-]"),
            replacement = "_",
        )
    }

    private fun sha256(value: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString(separator = "") { byte ->
                "%02x".format(byte)
            }
    }

    companion object {
        private const val TARGET_BITMAP_WIDTH = 1400
        private const val CONNECT_TIMEOUT_MILLIS = 15_000
        private const val READ_TIMEOUT_MILLIS = 30_000
        private const val USER_AGENT =
            "StatsDroid/1.0 (Probstat reference library)"
    }
}
