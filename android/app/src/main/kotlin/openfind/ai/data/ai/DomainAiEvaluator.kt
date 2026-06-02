package openfind.ai.data.ai

import android.app.ActivityManager
import android.content.Context
import openfind.ai.data.local.entity.AiModelInfo
import openfind.ai.data.native.OpenfindNative
import openfind.ai.domain.model.BrandScore

class DomainAiEvaluator(private val context: Context) {
    private val interpreter = AiInterpreter(context)

    val modelInfo: AiModelInfo
        get() {
            val mi = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            mi.getMemoryInfo(memInfo)
            val totalGb = (memInfo.totalMem ?: 4L * 1024 * 1024 * 1024) / (1024.0 * 1024.0 * 1024.0)

            return AiModelInfo(
                isLoaded = interpreter.isAiLoaded(),
                modelSizeMb = 15f,
                deviceRamGb = totalGb.toFloat(),
                isCompatible = memInfo.availMem / (1024.0 * 1024.0 * 1024.0) >= 2.5,
                recommended = memInfo.availMem / (1024.0 * 1024.0 * 1024.0) >= 4.0
            )
        }

    fun tryEnableAi(): Boolean = interpreter.tryLoad()

    fun disableAi(): Boolean {
        interpreter.unload()
        return true
    }

    fun evaluate(domain: String, useAi: Boolean): BrandScore {
        return if (useAi && interpreter.isAiLoaded()) {
            interpreter.evaluateBrand(domain)
        } else {
            OpenfindNative.evaluateBrandSafe(domain) ?: heuristicEvaluate(domain)
        }
    }

    private fun heuristicEvaluate(domain: String): BrandScore {
        val parts = domain.split(".", limit = 2)
        val name = parts[0]
        val tld = if (parts.size > 1) parts[1] else "com"

        val lenScore = when {
            name.length <= 4 -> 9.5f
            name.length <= 6 -> 8.5f
            name.length <= 8 -> 7.0f
            name.length <= 10 -> 5.5f
            name.length <= 12 -> 4.0f
            else -> 2.5f
        }

        val vowels = name.count { it in "aeiou" }
        val consonants = name.count { it in "bcdfghjklmnpqrstvwxyz" }
        val ratio = if (consonants > 0) vowels.toFloat() / consonants else 1f
        val pronScore = when {
            ratio in 0.35f..0.55f -> 8.5f
            ratio in 0.2f..0.7f -> 6.5f
            else -> 4.0f
        }.let { s -> if (name.contains(Regex("[bcdfghjklmnpqrstvwxyz]{3,}"))) s - 2f else s }
            .coerceIn(1f, 10f)

        val keywords = listOf(
            "ai", "bot", "neo", "tech", "labs", "hub", "zen", "app", "flow",
            "core", "net", "dev", "smart", "nova", "prime", "ultra", "alpha", "beta", "next",
            "cyber", "mind", "data", "open", "find", "cloud", "pixel", "deep", "fast", "meta"
        )
        val memScore = (7f + keywords.count { name.contains(it) } * 1.5f)
            .let { s -> if (name.contains('-')) s - 2f else s }
            .let { s -> if (name.any { it.isDigit() }) s - 1.5f else s }
            .coerceIn(1f, 10f)

        val tldScore = when (tld) {
            "com" -> 9.8f; "ai" -> 9.7f; "io" -> 9.0f
            "co" -> 8.5f; "net" -> 7.8f; "org" -> 7.5f
            else -> 6.5f
        }

        val finalScore = (pronScore * 0.25f + memScore * 0.35f + lenScore * 0.15f + tldScore * 0.25f)
            .coerceIn(1f, 10f)

        val feedback = when {
            finalScore >= 9f -> "Premium brand name. Highly recommended."
            finalScore >= 7.5f -> "Strong brand potential."
            finalScore >= 5.5f -> "Acceptable. Consider alternatives for better branding."
            else -> "Difficult to remember. Try the name generator."
        }

        return BrandScore(finalScore, feedback, lenScore, pronScore, memScore, tldScore)
    }
}
