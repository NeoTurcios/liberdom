package openfind.ai.domain.model

data class GeneratorConfig(
    val keyword: String,
    val selectedTlds: List<String>,
    val maxCount: Int
)
