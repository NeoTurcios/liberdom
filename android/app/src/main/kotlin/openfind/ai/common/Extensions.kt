package openfind.ai.common

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

fun String.isValidDomain(): Boolean {
    val pattern = Regex(
        "^(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+" +
        "[a-zA-Z]{2,63}$"
    )
    return pattern.matches(this.lowercase())
}

fun String.cleanDomain(): String {
    return this.trim()
        .lowercase()
        .replace(Regex("^https?://"), "")
        .replace(Regex("^www\\."), "")
        .split("/")[0]
        .trimEnd('.')
}

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("OpenFind AI", text)
    clipboard.setPrimaryClip(clip)
}

fun Activity.shareText(text: String, subject: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, subject))
}
