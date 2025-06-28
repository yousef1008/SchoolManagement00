// ViewModels
package com.schoolmanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolmanagement.domain.model.Student
import com.schoolmanagement.domain.model.StudentWithInstallments
import com.schoolmanagement.domain.usecase.student.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentUiState(
    val students: List<Student> = emptyList(),
    val studentsWithInstallments: List<StudentWithInstallments> = emptyList(),
    val overdueStudents: List<StudentWithInstallments> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

class StudentViewModel(
    private val getAllStudentsUseCase: GetAllStudentsUseCase,
    private val getStudentByIdUseCase: GetStudentByIdUseCase,
    private val addStudentUseCase: AddStudentUseCase,
    private val updateStudentUseCase: UpdateStudentUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase,
    private val searchStudentsUseCase: SearchStudentsUseCase,
    private val getOverdueStudentsUseCase: GetOverdueStudentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
        loadOverdueStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getAllStudentsUseCase().collect { students ->
                    _uiState.value = _uiState.value.copy(
                        students = students,
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

    fun loadOverdueStudents() {
        viewModelScope.launch {
            try {
                getOverdueStudentsUseCase().collect { overdueStudents ->
                    _uiState.value = _uiState.value.copy(
                        overdueStudents = overdueStudents
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun searchStudents(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            loadStudents()
            return
        }

        viewModelScope.launch {
            try {
                searchStudentsUseCase(query).collect { students ->
                    _uiState.value = _uiState.value.copy(
                        students = students,
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

    fun addStudent(student: Student) {
        viewModelScope.launch {
            try {
                addStudentUseCase(student)
                loadStudents()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            try {
                updateStudentUseCase(student)
                loadStudents()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            try {
                deleteStudentUseCase(student)
                loadStudents()
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

