package openfind.ai.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import openfind.ai.data.ai.AiInterpreter
import openfind.ai.data.native.OpenfindNative
import openfind.ai.data.repository.SettingsRepository
import openfind.ai.domain.model.BrandScore

class EvaluateBrandUseCase(
    private val aiInterpreter: AiInterpreter,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(domain: String): BrandScore {
        return if (settingsRepository.isAiEnabled) {
            withContext(Dispatchers.Default) {
                aiInterpreter.evaluateBrand(domain)
            }
        } else {
            withContext(Dispatchers.Default) {
                try {
                    OpenfindNative.evaluateBrand(domain)
                } catch (e: Exception) {
                    heuristicEvaluate(domain)
                }
            }
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
