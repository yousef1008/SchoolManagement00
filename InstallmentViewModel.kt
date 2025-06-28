// Installment ViewModel
package com.schoolmanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolmanagement.domain.model.Installment
import com.schoolmanagement.domain.usecase.installment.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InstallmentUiState(
    val installments: List<Installment> = emptyList(),
    val totalCollected: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class InstallmentViewModel(
    private val getAllInstallmentsUseCase: GetAllInstallmentsUseCase,
    private val getInstallmentsByStudentUseCase: GetInstallmentsByStudentUseCase,
    private val addInstallmentUseCase: AddInstallmentUseCase,
    private val updateInstallmentUseCase: UpdateInstallmentUseCase,
    private val deleteInstallmentUseCase: DeleteInstallmentUseCase,
    private val getTotalCollectedAmountUseCase: GetTotalCollectedAmountUseCase,
    private val getTotalPaidByStudentUseCase: GetTotalPaidByStudentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InstallmentUiState())
    val uiState: StateFlow<InstallmentUiState> = _uiState.asStateFlow()

    init {
        loadInstallments()
        loadTotalCollected()
    }

    fun loadInstallments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getAllInstallmentsUseCase().collect { installments ->
                    _uiState.value = _uiState.value.copy(
                        installments = installments,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun loadInstallmentsByStudent(studentId: String) {
        viewModelScope.launch {
            try {
                getInstallmentsByStudentUseCase(studentId).collect { installments ->
                    _uiState.value = _uiState.value.copy(
                        installments = installments,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun loadTotalCollected() {
        viewModelScope.launch {
            try {
                val total = getTotalCollectedAmountUseCase()
                _uiState.value = _uiState.value.copy(totalCollected = total)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun addInstallment(installment: Installment) {
        viewModelScope.launch {
            try {
                addInstallmentUseCase(installment)
                loadInstallments()
                loadTotalCollected()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun updateInstallment(installment: Installment) {
        viewModelScope.launch {
            try {
                updateInstallmentUseCase(installment)
                loadInstallments()
                loadTotalCollected()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun deleteInstallment(installment: Installment) {
        viewModelScope.launch {
            try {
                deleteInstallmentUseCase(installment)
                loadInstallments()
                loadTotalCollected()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    suspend fun getTotalPaidByStudent(studentId: String): Double {
        return try {
            getTotalPaidByStudentUseCase(studentId)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = e.message)
            0.0
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

