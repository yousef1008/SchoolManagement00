// Employee ViewModel
package com.schoolmanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolmanagement.domain.model.Employee
import com.schoolmanagement.domain.usecase.employee.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmployeeUiState(
    val employees: List<Employee> = emptyList(),
    val totalSalaries: Double = 0.0,
    val employeeCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

class EmployeeViewModel(
    private val getAllEmployeesUseCase: GetAllEmployeesUseCase,
    private val getEmployeeByIdUseCase: GetEmployeeByIdUseCase,
    private val addEmployeeUseCase: AddEmployeeUseCase,
    private val updateEmployeeUseCase: UpdateEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val searchEmployeesUseCase: SearchEmployeesUseCase,
    private val getTotalSalariesUseCase: GetTotalSalariesUseCase,
    private val getEmployeeCountUseCase: GetEmployeeCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    init {
        loadEmployees()
        loadTotalSalaries()
        loadEmployeeCount()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getAllEmployeesUseCase().collect { employees ->
                    _uiState.value = _uiState.value.copy(
                        employees = employees,
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

    fun loadTotalSalaries() {
        viewModelScope.launch {
            try {
                val total = getTotalSalariesUseCase()
                _uiState.value = _uiState.value.copy(totalSalaries = total)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun loadEmployeeCount() {
        viewModelScope.launch {
            try {
                val count = getEmployeeCountUseCase()
                _uiState.value = _uiState.value.copy(employeeCount = count)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun searchEmployees(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            loadEmployees()
            return
        }

        viewModelScope.launch {
            try {
                searchEmployeesUseCase(query).collect { employees ->
                    _uiState.value = _uiState.value.copy(
                        employees = employees,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                addEmployeeUseCase(employee)
                loadEmployees()
                loadTotalSalaries()
                loadEmployeeCount()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                updateEmployeeUseCase(employee)
                loadEmployees()
                loadTotalSalaries()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                deleteEmployeeUseCase(employee)
                loadEmployees()
                loadTotalSalaries()
                loadEmployeeCount()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

