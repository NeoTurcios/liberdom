package openfind.ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import openfind.ai.data.repository.DomainRepository
import openfind.ai.domain.model.GeneratorItem

data class GeneratorState(
    val keyword: String = "",
    val selectedTlds: Set<String> = setOf("com", "io"),
    val availableTlds: List<String> = listOf("com", "net", "org", "io", "app", "ai", "co", "me"),
    val generatedItems: List<GeneratorItem> = emptyList(),
    val isGenerating: Boolean = false,
    val isChecking: Boolean = false
)

class GeneratorViewModel(
    private val domainRepository: DomainRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GeneratorState())
    val state: StateFlow<GeneratorState> = _state.asStateFlow()

    fun onKeywordChange(keyword: String) {
        _state.update { it.copy(keyword = keyword) }
    }

    fun onToggleTld(tld: String) {
        _state.update { current ->
            val newTlds = current.selectedTlds.toMutableSet()
            if (tld in newTlds) newTlds.remove(tld) else newTlds.add(tld)
            current.copy(selectedTlds = newTlds)
        }
    }

    fun onGenerate() {
        val keyword = _state.value.keyword.trim().lowercase()
        if (keyword.length < 2) return

        val tlds = _state.value.selectedTlds.toList().ifEmpty { listOf("com") }

        _state.update { it.copy(isGenerating = true) }

        viewModelScope.launch {
            val names = withContext(Dispatchers.IO) {
                domainRepository.generateNames(keyword, tlds)
            }
            _state.update {
                it.copy(
                    generatedItems = names.map { n -> GeneratorItem(domain = n) },
                    isGenerating = false
                )
            }
        }
    }

    fun onCheckDomain(domain: String) {
        _state.update { it.copy(isChecking = true) }

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    domainRepository.checkDomain(domain)
                }
                val (score, _) = domainRepository.evaluateBrandHeuristic(domain)
                _state.update { current ->
                    val updatedItems = current.generatedItems.map { item ->
                        if (item.domain == domain) {
                            item.copy(
                                status = result.status,
                                brandScore = if (result.status == "available") score.toFloat() else null
                            )
                        } else item
                    }
                    current.copy(generatedItems = updatedItems, isChecking = false)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isChecking = false) }
            }
        }
    }
}
