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
import openfind.ai.data.local.dao.HistoryDao
import openfind.ai.data.local.dao.SavedDao
import openfind.ai.data.local.entity.HistoryEntity
import openfind.ai.data.local.entity.SavedEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LibraryState(
    val tabIndex: Int = 0,
    val tabMode: String = "saved",
    val savedItems: List<SavedEntity> = emptyList(),
    val historyItems: List<HistoryEntity> = emptyList(),
    val isLoading: Boolean = true,
    val itemCount: Int = 0
)

class LibraryViewModel(
    application: Application,
    private val savedDao: SavedDao,
    private val historyDao: HistoryDao
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            savedDao.getAll().collect { saved ->
                _state.update { it.copy(savedItems = saved) }
                refreshItemCount()
            }
        }
        viewModelScope.launch {
            historyDao.getAll().collect { history ->
                _state.update { it.copy(historyItems = history, isLoading = false) }
                refreshItemCount()
            }
        }
    }

    private fun refreshItemCount() {
        val current = _state.value
        val count = if (current.tabMode == "saved") current.savedItems.size else current.historyItems.size
        _state.update { it.copy(itemCount = count) }
    }

    fun onTabChange(index: Int) {
        val mode = if (index == 0) "saved" else "history"
        _state.update { it.copy(tabIndex = index, tabMode = mode) }
        refreshItemCount()
    }

    fun onDelete(type: String, domain: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                "saved" -> savedDao.deleteByDomain(domain)
                "history" -> historyDao.deleteByDomain(domain)
            }
        }
    }

    fun onDeleteHistoryById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.deleteById(id)
        }
    }

    fun onClear(type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                "saved" -> savedDao.clearAll()
                "history" -> historyDao.clearAll()
            }
        }
    }

    fun onExportPdf(saved: SavedEntity) {
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
                canvas.drawText("Domain: ${saved.domain}", 40f, 140f, paintBody)
                canvas.drawText("Status: ${saved.status}", 40f, 165f, paintBody)
                pdfDoc.finishPage(page)
                val cacheFile = File(context.cacheDir, "OpenFind_${saved.domain}_report.pdf")
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

    fun onExportPdf(history: HistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val pdfDoc = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDoc.startPage(pageInfo)
                val canvas = page.canvas
                val paintBody = Paint().apply {
                    color = android.graphics.Color.parseColor("#111827"); textSize = 12f
                }
                canvas.drawText("Domain: ${history.domain}", 40f, 140f, paintBody)
                canvas.drawText("Status: ${history.status}", 40f, 165f, paintBody)
                pdfDoc.finishPage(page)
                val cacheFile = File(context.cacheDir, "OpenFind_${history.domain}_report.pdf")
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

    fun onShare(saved: SavedEntity) {
        val context = getApplication<Application>()
        val text = "OpenFind AI - ${saved.domain}: ${saved.status}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share"))
    }

    fun onShare(history: HistoryEntity) {
        val context = getApplication<Application>()
        val text = "OpenFind AI - ${history.domain}: ${history.status}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share"))
    }
}
