package openfind.ai.domain.model

data class BrandScore(
    val score: Float,
    val feedback: String,
    val lengthScore: Float,
    val pronounceScore: Float,
    val memoryScore: Float,
    val tldScore: Float
)
