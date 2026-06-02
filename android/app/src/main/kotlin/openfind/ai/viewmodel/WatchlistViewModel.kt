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
import openfind.ai.data.local.entity.WatchlistEntity
import openfind.ai.data.repository.WatchlistRepository

data class WatchlistState(
    val watchlist: List<WatchlistEntity> = emptyList(),
    val isAdding: Boolean = false,
    val newDomainInput: String = "",
    val newLabelInput: String = "",
    val selectedInterval: Int = 24
)

class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistState())
    val state: StateFlow<WatchlistState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            watchlistRepository.watchlist.collect { items ->
                _state.update { it.copy(watchlist = items) }
            }
        }
    }

    fun onNewDomainInputChange(input: String) {
        _state.update { it.copy(newDomainInput = input) }
    }

    fun onNewLabelInputChange(input: String) {
        _state.update { it.copy(newLabelInput = input) }
    }

    fun onSelectInterval(hours: Int) {
        _state.update { it.copy(selectedInterval = hours) }
    }

    fun onAdd() {
        val domain = _state.value.newDomainInput.trim().lowercase()
        if (domain.isBlank()) return

        viewModelScope.launch {
            watchlistRepository.add(
                domain = domain,
                label = _state.value.newLabelInput.trim(),
                intervalHours = _state.value.selectedInterval
            )
            _state.update {
                it.copy(newDomainInput = "", newLabelInput = "", selectedInterval = 24)
            }
        }
    }

    fun onRemove(domain: String) {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistRepository.remove(domain)
        }
    }

    fun onToggleNotify(domain: String) {
        val item = _state.value.watchlist.find { it.domain == domain } ?: return
        viewModelScope.launch(Dispatchers.IO) {
            watchlistRepository.toggleNotify(domain, !item.notifyEnabled)
        }
    }

    fun onChangeInterval(domain: String, hours: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistRepository.changeInterval(domain, hours)
        }
    }
}
