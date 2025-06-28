// Domain Models
package com.schoolmanagement.domain.model

data class Student(
    val studentId: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val address: String,
    val phoneNumber: String?,
    val parentName: String,
    val parentPhoneNumber: String,
    val grade: String,
    val enrollmentDate: String,
    val totalTuition: Double
) {
    val fullName: String
        get() = "$firstName $lastName"
    
    fun getRemainingTuition(totalPaid: Double): Double {
        return totalTuition - totalPaid
    }
}

data class Installment(
    val installmentId: String,
    val studentId: String,
    val amountPaid: Double,
    val paymentDate: String,
    val paymentMethod: String,
    val description: String?
)

data class Employee(
    val employeeId: String,
    val firstName: String,
    val lastName: String,
    val position: String,
    val phoneNumber: String,
    val address: String,
    val salary: Double,
    val hireDate: String
) {
    val fullName: String
        get() = "$firstName $lastName"
}

data class User(
    val userId: String,
    val username: String,
    val passwordHash: String,
    val role: String,
    val employeeId: String?
)

data class StudentWithInstallments(
    val student: Student,
    val installments: List<Installment>,
    val totalPaid: Double,
    val remainingAmount: Double
)

data class DashboardStats(
    val totalStudents: Int,
    val totalCollected: Double,
    val totalEmployees: Int,
    val messagesSent: Int,
    val overdueStudents: List<StudentWithInstallments>
)

