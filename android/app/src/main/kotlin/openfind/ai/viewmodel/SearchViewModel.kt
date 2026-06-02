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
import openfind.ai.data.repository.SettingsRepository
import openfind.ai.domain.model.DomainResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SearchState(
    val domainInput: String = "",
    val isLoading: Boolean = false,
    val result: DomainResult? = null,
    val error: String? = null,
    val isAuditEnabled: Boolean = true,
    val isAiEnabled: Boolean = false,
    val isResultSaved: Boolean = false,
    val language: String = "es"
)

class SearchViewModel(
    application: Application,
    private val domainRepository: DomainRepository,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SearchState(
        isAiEnabled = settingsRepository.isAiEnabled,
        language = settingsRepository.language
    ))
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onDomainInputChange(input: String) {
        _state.update { it.copy(domainInput = input) }
    }

    fun onToggleAudit(enabled: Boolean) {
        _state.update { it.copy(isAuditEnabled = enabled) }
    }

    fun onToggleLanguage() {
        val newLang = if (_state.value.language == "es") "en" else "es"
        _state.update { it.copy(language = newLang) }
        settingsRepository.setLanguage(newLang)
    }

    fun onSearch() {
        val domain = _state.value.domainInput.trim()
        if (domain.isBlank()) {
            _state.update { it.copy(error = "Please enter a domain") }
            return
        }

        val clean = domain.lowercase()
            .replace("https://", "")
            .replace("http://", "")
            .replace("www.", "")
            .split("/")[0]

        if (!clean.contains(".") || clean.length < 4) {
            _state.update { it.copy(error = "Invalid domain format") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null, result = null) }

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    domainRepository.checkDomain(clean, _state.value.isAuditEnabled)
                }

                val enrichedResult = if (_state.value.isAiEnabled && result.status == "available") {
                    val (score, feedback) = domainRepository.evaluateBrandHeuristic(result.domain)
                    result.copy(brandScore = score, brandFeedback = feedback)
                } else {
                    result
                }

                val isSaved = withContext(Dispatchers.IO) {
                    domainRepository.savedDao.getByDomain(enrichedResult.domain) != null
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        result = enrichedResult,
                        error = null,
                        isResultSaved = isSaved
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun onSave() {
        val result = _state.value.result ?: return
        viewModelScope.launch(Dispatchers.IO) {
            if (_state.value.isResultSaved) {
                domainRepository.savedDao.deleteByDomain(result.domain)
                _state.update { it.copy(isResultSaved = false) }
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
                _state.update { it.copy(isResultSaved = true) }
            }
        }
    }

    fun onExportPdf() {
        val result = _state.value.result ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val pdfDoc = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDoc.startPage(pageInfo)
                val canvas = page.canvas

                val paintTitle = Paint().apply {
                    color = android.graphics.Color.parseColor("#070b13")
                    textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val paintBody = Paint().apply {
                    color = android.graphics.Color.parseColor("#111827")
                    textSize = 12f
                }
                val paintBold = Paint().apply {
                    color = android.graphics.Color.parseColor("#111827")
                    textSize = 12f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }

                val headerBg = Paint().apply { color = android.graphics.Color.parseColor("#CC111827") }
                canvas.drawRect(0f, 0f, 595f, 100f, headerBg)
                paintTitle.color = android.graphics.Color.WHITE
                canvas.drawText("OpenFind AI Domain Report", 30f, 55f, paintTitle)
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val paintSub = Paint().apply { color = android.graphics.Color.parseColor("#9CA3AF"); textSize = 12f }
                canvas.drawText("Generated: $dateStr", 30f, 80f, paintSub)

                var y = 150f
                fun drawRow(label: String, value: String) {
                    canvas.drawText(label, 40f, y, paintBold)
                    canvas.drawText(value, 220f, y, paintBody)
                    y += 25f
                }

                drawRow("Domain:", result.domain.uppercase())
                drawRow("Status:", result.status)
                drawRow("Detail:", result.detail)
                drawRow("Method:", result.method)
                if (!result.ip.isNullOrEmpty()) drawRow("IP:", result.ip)
                if (!result.registrar.isNullOrEmpty()) drawRow("Registrar:", result.registrar)
                if (!result.creationDate.isNullOrEmpty()) drawRow("Created:", result.creationDate)
                result.brandScore?.let { drawRow("AI Score:", "%.1f/10".format(it)) }

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
                        putExtra(Intent.EXTRA_SUBJECT, "OpenFind Domain Report: ${result.domain}")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(
                        Intent.createChooser(intent, "Export PDF Report").apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun onShare() {
        val result = _state.value.result ?: return
        val context = getApplication<Application>()
        val shareText = """
OpenFind AI - Domain Analysis:

Domain: ${result.domain.uppercase()}
Status: ${if (result.status == "available") "AVAILABLE (FREE)!" else "Taken"}
IP: ${result.ip ?: "None"}
Registrar: ${result.registrar ?: "N/A"}
Method: ${result.method}

Check domains for free with OpenFind AI Android from neopunto.com!
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "OpenFind Check: ${result.domain}")
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share domain"))
    }

    fun onCopy() {
        val result = _state.value.result ?: return
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("OpenFind AI", result.domain)
        clipboard.setPrimaryClip(clip)
    }
}
