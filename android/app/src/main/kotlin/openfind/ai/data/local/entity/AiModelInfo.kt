package openfind.ai.data.local.entity

data class AiModelInfo(
    val isLoaded: Boolean,
    val modelSizeMb: Float,
    val deviceRamGb: Float,
    val isCompatible: Boolean,
    val recommended: Boolean
)
