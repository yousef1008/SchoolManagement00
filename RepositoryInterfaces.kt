// Repository Interfaces
package com.schoolmanagement.domain.repository

import com.schoolmanagement.domain.model.*
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getAllStudents(): Flow<List<Student>>
    suspend fun getStudentById(studentId: String): Student?
    fun searchStudents(searchQuery: String): Flow<List<Student>>
    fun getStudentsByGrade(grade: String): Flow<List<Student>>
    suspend fun insertStudent(student: Student)
    suspend fun updateStudent(student: Student)
    suspend fun deleteStudent(student: Student)
    suspend fun getStudentCount(): Int
    suspend fun getStudentsWithInstallments(): Flow<List<StudentWithInstallments>>
    suspend fun getOverdueStudents(): Flow<List<StudentWithInstallments>>
}

interface InstallmentRepository {
    fun getAllInstallments(): Flow<List<Installment>>
    fun getInstallmentsByStudentId(studentId: String): Flow<List<Installment>>
    suspend fun getInstallmentById(installmentId: String): Installment?
    suspend fun getTotalPaidByStudent(studentId: String): Double
    suspend fun getTotalCollectedAmount(): Double
    suspend fun insertInstallment(installment: Installment)
    suspend fun updateInstallment(installment: Installment)
    suspend fun deleteInstallment(installment: Installment)
    suspend fun getInstallmentCount(): Int
}

interface EmployeeRepository {
    fun getAllEmployees(): Flow<List<Employee>>
    suspend fun getEmployeeById(employeeId: String): Employee?
    fun searchEmployees(searchQuery: String): Flow<List<Employee>>
    fun getEmployeesByPosition(position: String): Flow<List<Employee>>
    suspend fun getTotalSalaries(): Double
    suspend fun insertEmployee(employee: Employee)
    suspend fun updateEmployee(employee: Employee)
    suspend fun deleteEmployee(employee: Employee)
    suspend fun getEmployeeCount(): Int
}

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>
    suspend fun getUserById(userId: String): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun authenticateUser(username: String, password: String): User?
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun getUserCount(): Int
}

interface WhatsAppRepository {
    suspend fun sendMessage(phoneNumber: String, message: String): Boolean
    suspend fun sendBulkMessages(recipients: List<Pair<String, String>>): Map<String, Boolean>
    suspend fun getMessageHistory(): Flow<List<WhatsAppMessage>>
}

data class WhatsAppMessage(
    val messageId: String,
    val recipient: String,
    val phoneNumber: String,
    val message: String,
    val status: String,
    val sentDate: String
)

