package openfind.ai.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import openfind.ai.data.local.entity.SavedEntity
import openfind.ai.data.repository.DomainRepository
import openfind.ai.domain.model.BulkStats
import openfind.ai.domain.model.DomainResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BulkState(
    val bulkInput: String = "",
    val results: List<DomainResult> = emptyList(),
    val isRunning: Boolean = false,
    val progress: Float = 0f,
    val stats: BulkStats = BulkStats()
)

class BulkViewModel(
    application: Application,
    private val domainRepository: DomainRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(BulkState())
    val state: StateFlow<BulkState> = _state.asStateFlow()

    fun onBulkInputChange(input: String) {
        _state.update { it.copy(bulkInput = input) }
    }

    fun onBulkCheck() {
        val rawInput = _state.value.bulkInput.trim()
        if (rawInput.isBlank()) return

        val domains = rawInput.split("\n")
            .map { line ->
                line.trim().lowercase()
                    .replace("https://", "")
                    .replace("http://", "")
                    .replace("www.", "")
                    .split("/")[0]
            }
            .filter { it.isNotEmpty() && it.contains(".") }

        if (domains.isEmpty()) return

        _state.update {
            it.copy(
                isRunning = true,
                results = emptyList(),
                progress = 0f,
                stats = BulkStats(total = domains.size)
            )
        }

        val results = mutableListOf<DomainResult>()

        viewModelScope.launch {
            for ((index, domain) in domains.withIndex()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        domainRepository.checkDomain(domain)
                    }
                    results.add(result)
                    val stats = _state.value.stats
                    _state.update {
                        it.copy(
                            results = results.toList(),
                            progress = (index + 1).toFloat() / domains.size,
                            stats = stats.copy(
                                checked = index + 1,
                                free = if (result.status == "available") stats.free + 1 else stats.free,
                                taken = if (result.status != "available") stats.taken + 1 else stats.taken
                            )
                        )
                    }
                    kotlinx.coroutines.delay(300)
                } catch (_: Exception) {
                    _state.update {
                        it.copy(
                            progress = (index + 1).toFloat() / domains.size,
                            stats = it.stats.copy(checked = index + 1)
                        )
                    }
                }
            }
            _state.update { it.copy(isRunning = false) }
        }
    }

    fun onClearResults() {
        _state.update { it.copy(results = emptyList(), stats = BulkStats(), progress = 0f) }
    }

    fun onToggleSave(result: DomainResult) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = domainRepository.savedDao.getByDomain(result.domain)
                if (existing != null) {
                    domainRepository.savedDao.deleteByDomain(result.domain)
                } else {
                    domainRepository.savedDao.insert(
                        SavedEntity(
                            domain = result.domain,
                            status = result.status,
                            detail = result.detail,
                            method = result.method,
                            ip = result.ip,
                            registrar = result.registrar,
                            creationDate = result.creationDate,
                            sslActive = result.sslActive,
                            sslIssuer = result.sslIssuer,
                            cloudflare = result.cloudflare,
                            nsServers = result.nsServers.joinToString(",")
                        )
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun onExportPdf(result: DomainResult) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val pdfDoc = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDoc.startPage(pageInfo)
                val canvas = page.canvas
                val paintTitle = Paint().apply {
                    color = android.graphics.Color.parseColor("#070b13"); textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val paintBody = Paint().apply {
                    color = android.graphics.Color.parseColor("#111827"); textSize = 12f
                }
                val headerBg = Paint().apply { color = android.graphics.Color.parseColor("#CC111827") }
                canvas.drawRect(0f, 0f, 595f, 100f, headerBg)
                paintTitle.color = android.graphics.Color.WHITE
                canvas.drawText("OpenFind AI Domain Report", 30f, 55f, paintTitle)
                canvas.drawText("Domain: ${result.domain}", 40f, 140f, paintBody)
                canvas.drawText("Status: ${result.status}", 40f, 165f, paintBody)
                pdfDoc.finishPage(page)
                val cacheFile = File(
                    context.cacheDir,
                    "OpenFind_${result.domain.replace(".", "_")}_report.pdf"
                )
                FileOutputStream(cacheFile).use { pdfDoc.writeTo(it) }
                pdfDoc.close()
                withContext(Dispatchers.Main) {
                    val fileUri = FileProvider.getUriForFile(
                        context, "openfind.ai.fileprovider", cacheFile
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(Intent.createChooser(intent, "Export PDF"))
                }
            } catch (_: Exception) {}
        }
    }

    fun onShare(result: DomainResult) {
        val context = getApplication<Application>()
        val text = "OpenFind AI - ${result.domain.uppercase()}: ${result.status}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share"))
    }

    fun onCopy(result: DomainResult) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("OpenFind", result.domain))
    }
}
