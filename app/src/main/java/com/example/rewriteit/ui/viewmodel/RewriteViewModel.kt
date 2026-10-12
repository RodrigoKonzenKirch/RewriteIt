package com.example.rewriteit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewriteit.data.MlKitRewriterRepository
import com.example.rewriteit.data.RewriterRepository
import com.example.rewriteit.model.ModelStatus
import com.example.rewriteit.model.RewriteHistoryItem
import com.example.rewriteit.model.RewriteStyle
import com.example.rewriteit.model.RewriteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RewriteViewModel(
    private val repository: RewriterRepository = MlKitRewriterRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RewriteUiState())
    val uiState: StateFlow<RewriteUiState> = _uiState.asStateFlow()

    init {
        checkModelStatus()
    }

    fun onInputTextChanged(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(
                inputText = newText,
                errorMessage = if (newText.isNotBlank()) null else currentState.errorMessage
            )
        }
    }

    fun onStyleSelected(style: RewriteStyle) {
        _uiState.update { currentState ->
            currentState.copy(selectedStyle = style)
        }
    }

    fun checkModelStatus() {
        viewModelScope.launch {
            repository.checkModelStatus().collect { status ->
                _uiState.update { currentState ->
                    currentState.copy(modelStatus = status)
                }
            }
        }
    }

    fun downloadModel() {
        if (_uiState.value.modelStatus == ModelStatus.DOWNLOADING) return

        viewModelScope.launch {
            _uiState.update { it.copy(modelStatus = ModelStatus.DOWNLOADING, downloadProgress = 0.1f) }
            val success = repository.downloadModel()
            _uiState.update { currentState ->
                currentState.copy(
                    modelStatus = if (success) ModelStatus.READY else ModelStatus.ERROR,
                    downloadProgress = if (success) 1.0f else 0f
                )
            }
        }
    }

    fun rewriteText() {
        val currentInput = _uiState.value.inputText
        val currentStyle = _uiState.value.selectedStyle

        if (currentInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter text to rewrite") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.rewrite(currentInput, currentStyle)

            result.fold(
                onSuccess = { outputText ->
                    val historyEntry = RewriteHistoryItem(
                        originalText = currentInput,
                        style = currentStyle,
                        rewrittenText = outputText
                    )
                    _uiState.update { currentState ->
                        currentState.copy(
                            rewrittenResult = outputText,
                            isLoading = false,
                            errorMessage = null,
                            history = listOf(historyEntry) + currentState.history
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = throwable.localizedMessage ?: "Failed to rewrite text"
                        )
                    }
                }
            )
        }
    }

    fun clearInput() {
        _uiState.update { it.copy(inputText = "", errorMessage = null) }
    }

    fun clearResult() {
        _uiState.update { it.copy(rewrittenResult = "") }
    }

    fun swapResultToInput() {
        val currentResult = _uiState.value.rewrittenResult
        if (currentResult.isNotBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    inputText = currentResult,
                    rewrittenResult = ""
                )
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
