package openfind.ai.common

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import openfind.ai.domain.model.BrandScore
import openfind.ai.domain.model.DomainResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    fun exportDomainReport(
        context: Context,
        result: DomainResult,
        brandScore: BrandScore?
    ): File {
        val pdfDoc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDoc.startPage(pageInfo)
        val canvas = page.canvas

        val paintTitle = Paint().apply {
            color = Color.parseColor("#070b13")
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintSub = Paint().apply {
            color = Color.parseColor("#4B5563")
            textSize = 12f
        }

        val paintSection = Paint().apply {
            color = Color.parseColor("#1F2937")
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintBody = Paint().apply {
            color = Color.parseColor("#111827")
            textSize = 12f
        }

        val paintBold = Paint().apply {
            color = Color.parseColor("#111827")
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintDivider = Paint().apply {
            color = Color.parseColor("#E5E7EB")
            strokeWidth = 2f
        }

        val headerBg = Paint().apply { color = Color.parseColor("#CC111827") }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 100f, headerBg)

        paintTitle.color = Color.WHITE
        canvas.drawText("OpenFind AI Domain Analysis Report", 30f, 55f, paintTitle)

        paintSub.color = Color.parseColor("#9CA3AF")
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        canvas.drawText("Generated on: $dateStr", 30f, 80f, paintSub)

        paintSection.color = Color.parseColor("#111827")
        canvas.drawText("1. Domain Summary", 40f, 140f, paintSection)
        canvas.drawLine(40f, 150f, (PAGE_WIDTH - 40).toFloat(), 150f, paintDivider)

        canvas.drawText("Analyzed Domain:", 40f, 180f, paintBody)
        val paintDomainText = Paint().apply {
            color = Color.parseColor("#8b5cf6")
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(result.domain.uppercase(Locale.getDefault()), 180f, 182f, paintDomainText)

        canvas.drawText("Availability Status:", 40f, 215f, paintBody)

        val badgeColor = when (result.status) {
            DomainResult.STATUS_AVAILABLE -> Color.parseColor("#00e676")
            DomainResult.STATUS_TAKEN -> Color.parseColor("#ef4444")
            else -> Color.parseColor("#eab308")
        }
        val paintBadge = Paint().apply { color = badgeColor }
        canvas.drawRoundRect(180f, 200f, 320f, 225f, 6f, 6f, paintBadge)

        val paintBadgeText = Paint().apply {
            color = if (result.status == DomainResult.STATUS_AVAILABLE) Color.BLACK else Color.WHITE
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val badgeLabel = when (result.status) {
            DomainResult.STATUS_AVAILABLE -> "AVAILABLE (FREE)"
            DomainResult.STATUS_TAKEN -> "TAKEN (REGISTERED)"
            else -> "UNKNOWN"
        }
        canvas.drawText(badgeLabel, 250f, 217f, paintBadgeText)

        val desc = when (result.status) {
            DomainResult.STATUS_AVAILABLE ->
                "Congratulations! This domain is free. Locally analyzed by OpenFind AI."
            DomainResult.STATUS_TAKEN ->
                "Domain already taken. It is purchased and registered on the internet."
            else ->
                "Could not be reliably determined. WHOIS rate limit or unsupported TLD."
        }
        canvas.drawText(desc, 40f, 255f, paintBody)

        canvas.drawText("2. Technical Specifications", 40f, 310f, paintSection)
        canvas.drawLine(40f, 320f, (PAGE_WIDTH - 40).toFloat(), 320f, paintDivider)

        var y = 350f
        fun drawTableRow(label: String, value: String) {
            canvas.drawText(label, 50f, y, paintBold)
            canvas.drawText(value, 220f, y, paintBody)
            canvas.drawLine(40f, y + 8f, (PAGE_WIDTH - 40).toFloat(), y + 8f,
                Paint().apply { color = Color.parseColor("#F3F4F6"); strokeWidth = 1f })
            y += 30f
        }

        drawTableRow("Technical Status Code:", result.status.uppercase(Locale.getDefault()))
        drawTableRow("Detection Method:", result.method)
        drawTableRow("IP Address:", result.ip ?: "N/A (No active DNS resolution)")
        drawTableRow("Registrar:", result.registrar ?: "N/A")
        drawTableRow("Creation Date:", result.creationDate ?: "N/A")

        if (result.sslActive) {
            drawTableRow("SSL Status:", "Active")
            if (!result.sslIssuer.isNullOrEmpty()) {
                drawTableRow("SSL Issuer:", result.sslIssuer)
            }
        }
        if (result.cloudflare != DomainResult.CLOUDFLARE_NONE) {
            val cfLabel = when (result.cloudflare) {
                DomainResult.CLOUDFLARE_ORANGE -> "Orange Cloud (Proxy Active)"
                DomainResult.CLOUDFLARE_GRAY -> "Gray Cloud (DNS Only)"
                else -> "Detected"
            }
            drawTableRow("Cloudflare:", cfLabel)
        }
        if (result.nsServers.isNotEmpty()) {
            drawTableRow("Name Servers:", result.nsServers.take(3).joinToString(", "))
        }

        canvas.drawText("3. Branding Evaluation", 40f, y + 20f, paintSection)
        canvas.drawLine(40f, y + 30f, (PAGE_WIDTH - 40).toFloat(), y + 30f, paintDivider)
        y += 55f

        if (brandScore != null) {
            canvas.drawText("Agent Rating Score: ${"%.1f".format(brandScore.score)}/10", 40f, y, paintBold)
            y += 25f
            canvas.drawText("Branding Analysis Feedback:", 40f, y, paintBold)
            y += 20f

            val feedbackLines = chunkText(brandScore.feedback, 70)
            for (line in feedbackLines) {
                canvas.drawText(line, 60f, y, paintBody)
                y += 20f
            }
        } else {
            canvas.drawText("AI evaluation not available for this domain.", 40f, y, paintBody)
            y += 25f
        }

        val footerBg = Paint().apply { color = Color.parseColor("#F9FAFB") }
        canvas.drawRect(0f, 790f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), footerBg)

        val paintFooterText = Paint().apply {
            color = Color.parseColor("#9CA3AF")
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Generated natively by OpenFind Android Client.", 297f, 810f, paintFooterText)
        canvas.drawText("Non-Commercial Evaluation License | openfind.ai", 297f, 825f, paintFooterText)

        pdfDoc.finishPage(page)

        val cacheDir = File(context.cacheDir, Constants.PDF_CACHE_DIR)
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val outputFile = File(cacheDir, "OpenFind_${result.domain.replace(".", "_")}_report.pdf")
        val outputStream = FileOutputStream(outputFile)
        pdfDoc.writeTo(outputStream)
        outputStream.flush()
        outputStream.close()
        pdfDoc.close()

        return outputFile
    }

    private fun chunkText(text: String, maxLen: Int): List<String> {
        val lines = mutableListOf<String>()
        var remaining = text
        while (remaining.length > maxLen) {
            val breakIdx = remaining.lastIndexOf(' ', maxLen)
            if (breakIdx <= 0) {
                lines.add(remaining.substring(0, maxLen) + "-")
                remaining = remaining.substring(maxLen)
            } else {
                lines.add(remaining.substring(0, breakIdx))
                remaining = remaining.substring(breakIdx + 1)
            }
        }
        if (remaining.isNotEmpty()) lines.add(remaining)
        return lines
    }
}
