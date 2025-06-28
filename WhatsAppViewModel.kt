// WhatsApp ViewModel
package com.schoolmanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolmanagement.domain.model.WhatsAppMessage
import com.schoolmanagement.domain.model.StudentWithInstallments
import com.schoolmanagement.domain.repository.WhatsAppRepository
import com.schoolmanagement.data.whatsapp.WhatsAppTemplates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WhatsAppUiState(
    val messages: List<WhatsAppMessage> = emptyList(),
    val sentCount: Int = 0,
    val failedCount: Int = 0,
    val successRate: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSendingBulk: Boolean = false
)

class WhatsAppViewModel(
    private val whatsAppRepository: WhatsAppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WhatsAppUiState())
    val uiState: StateFlow<WhatsAppUiState> = _uiState.asStateFlow()

    init {
        loadMessageHistory()
        calculateStatistics()
    }

    fun loadMessageHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                whatsAppRepository.getMessageHistory().collect { messages ->
                    _uiState.value = _uiState.value.copy(
                        messages = messages,
                        isLoading = false,
                        errorMessage = null
                    )
                    calculateStatistics()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun sendSingleMessage(phoneNumber: String, message: String) {
        viewModelScope.launch {
            try {
                val success = whatsAppRepository.sendMessage(phoneNumber, message)
                if (!success) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "فشل في إرسال الرسالة"
                    )
                }
                loadMessageHistory()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun sendOverduePaymentReminders(overdueStudents: List<StudentWithInstallments>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingBulk = true)
            try {
                val messages = overdueStudents.map { studentWithInstallments ->
                    val student = studentWithInstallments.student
                    val message = WhatsAppTemplates.getOverduePaymentMessage(
                        studentName = student.fullName,
                        parentName = student.parentName,
                        amount = studentWithInstallments.remainingAmount,
                        dueDate = "متأخر"
                    )
                    student.parentPhoneNumber to message
                }
                
                whatsAppRepository.sendBulkMessages(messages)
                loadMessageHistory()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            } finally {
                _uiState.value = _uiState.value.copy(isSendingBulk = false)
            }
        }
    }

    fun sendPaymentConfirmation(studentName: String, parentName: String, parentPhone: String, amount: Double, paymentDate: String) {
        viewModelScope.launch {
            try {
                val message = WhatsAppTemplates.getPaymentConfirmationMessage(
                    studentName = studentName,
                    parentName = parentName,
                    amount = amount,
                    paymentDate = paymentDate
                )
                
                whatsAppRepository.sendMessage(parentPhone, message)
                loadMessageHistory()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun sendGeneralAnnouncement(recipients: List<String>, title: String, content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingBulk = true)
            try {
                val message = WhatsAppTemplates.getGeneralAnnouncementMessage(title, content)
                val messages = recipients.map { phoneNumber ->
                    phoneNumber to message
                }
                
                whatsAppRepository.sendBulkMessages(messages)
                loadMessageHistory()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            } finally {
                _uiState.value = _uiState.value.copy(isSendingBulk = false)
            }
        }
    }

    private fun calculateStatistics() {
        val messages = _uiState.value.messages
        val sentCount = messages.count { it.status == "مرسل" }
        val failedCount = messages.count { it.status == "فشل" }
        val totalCount = messages.size
        
        val successRate = if (totalCount > 0) {
            (sentCount.toDouble() / totalCount.toDouble()) * 100
        } else {
            0.0
        }
        
        _uiState.value = _uiState.value.copy(
            sentCount = sentCount,
            failedCount = failedCount,
            successRate = successRate
        )
    }

    fun retryFailedMessage(message: WhatsAppMessage) {
        viewModelScope.launch {
            try {
                whatsAppRepository.sendMessage(message.phoneNumber, message.message)
                loadMessageHistory()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

