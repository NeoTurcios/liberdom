package openfind.ai.data.ai

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import openfind.ai.domain.model.BrandScore
import org.tensorflow.lite.Interpreter
import java.io.File

class AiInterpreter(private val context: Context) {
    private var interpreter: Interpreter? = null
    private var isLoaded = false

    fun tryLoad(): Boolean {
        isLoaded = true
        Log.i("AiInterpreter", "Local heuristic AI engine initialized successfully.")
        return true
    }

    fun evaluateBrand(domainName: String): BrandScore {
        if (!isLoaded || interpreter == null) {
            return heuristicEvaluate(domainName)
        }

        val parts = domainName.split(".", limit = 2)
        val name = parts[0]
        val tld = if (parts.size > 1) parts[1] else "com"

        try {
            val input = prepareInput(name, tld)
            val output = Array(1) { FloatArray(4) }
            interpreter?.run(input, output)

            val scores = output[0]
            val finalScore = (scores[0] * 0.25f + scores[1] * 0.35f + scores[2] * 0.15f + scores[3] * 0.25f)
                .coerceIn(1.0f, 10.0f)

            return BrandScore(
                score = finalScore,
                feedback = generateFeedback(finalScore, domainName),
                lengthScore = scores[0],
                pronounceScore = scores[1],
                memoryScore = scores[2],
                tldScore = scores[3]
            )
        } catch (e: Exception) {
            Log.w("AiInterpreter", "Inference failed, falling back to heuristic", e)
            return heuristicEvaluate(domainName)
        }
    }

    private fun prepareInput(name: String, tld: String): Array<FloatArray> {
        val features = FloatArray(128)
        val chars = "$name.$tld".lowercase().take(64)
        chars.forEachIndexed { i, c ->
            if (i < 64) features[i] = (c.code.toFloat() - 97f) / 26f
        }
        features[64] = name.length.toFloat() / 30f
        val commonTlds = listOf("com", "net", "org", "io", "ai", "co", "me", "app", "dev", "xyz")
        val tldIdx = commonTlds.indexOf(tld)
        if (tldIdx >= 0) features[65 + tldIdx] = 1f
        return arrayOf(features)
    }

    private fun generateFeedback(score: Float, domain: String): String {
        return when {
            score >= 9.0f -> "S-Tier brand name. Ultra-premium, short, and highly memorable."
            score >= 7.5f -> "Strong brand potential. Good balance of length and memorability."
            score >= 5.5f -> "Commercially acceptable. Consider shorter alternatives for premium appeal."
            score >= 3.5f -> "Below average memorability. Try the name generator for better options."
            else -> "Complex and hard to remember. Highly recommend using the name generator."
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
        }.let { s ->
            if (name.contains(Regex("[bcdfghjklmnpqrstvwxyz]{3,}"))) s - 2f else s
        }.coerceIn(1f, 10f)

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

    fun isAiLoaded(): Boolean = isLoaded

    fun unload() {
        interpreter?.close()
        interpreter = null
        isLoaded = false
    }
}
