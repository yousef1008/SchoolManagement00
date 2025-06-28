// Employee Repository Implementation
package com.schoolmanagement.data.repositories

import com.schoolmanagement.data.local.dao.EmployeeDao
import com.schoolmanagement.data.local.entities.EmployeeEntity
import com.schoolmanagement.domain.model.Employee
import com.schoolmanagement.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmployeeRepositoryImpl(
    private val employeeDao: EmployeeDao
) : EmployeeRepository {

    override fun getAllEmployees(): Flow<List<Employee>> {
        return employeeDao.getAllEmployees().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getEmployeeById(employeeId: String): Employee? {
        return employeeDao.getEmployeeById(employeeId)?.toDomainModel()
    }

    override fun searchEmployees(searchQuery: String): Flow<List<Employee>> {
        return employeeDao.searchEmployees(searchQuery).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getEmployeesByPosition(position: String): Flow<List<Employee>> {
        return employeeDao.getEmployeesByPosition(position).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTotalSalaries(): Double {
        return employeeDao.getTotalSalaries() ?: 0.0
    }

    override suspend fun insertEmployee(employee: Employee) {
        employeeDao.insertEmployee(employee.toEntity())
    }

    override suspend fun updateEmployee(employee: Employee) {
        employeeDao.updateEmployee(employee.toEntity())
    }

    override suspend fun deleteEmployee(employee: Employee) {
        employeeDao.deleteEmployee(employee.toEntity())
    }

    override suspend fun getEmployeeCount(): Int {
        return employeeDao.getEmployeeCount()
    }
}

// Extension functions for mapping
private fun EmployeeEntity.toDomainModel(): Employee {
    return Employee(
        employeeId = employeeId,
        firstName = firstName,
        lastName = lastName,
        position = position,
        phoneNumber = phoneNumber,
        address = address,
        salary = salary,
        hireDate = hireDate
    )
}

private fun Employee.toEntity(): EmployeeEntity {
    return EmployeeEntity(
        employeeId = employeeId,
        firstName = firstName,
        lastName = lastName,
        position = position,
        phoneNumber = phoneNumber,
        address = address,
        salary = salary,
        hireDate = hireDate
    )
}

