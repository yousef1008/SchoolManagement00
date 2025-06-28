// Repository Implementations
package com.schoolmanagement.data.repositories

import com.schoolmanagement.data.local.dao.StudentDao
import com.schoolmanagement.data.local.dao.InstallmentDao
import com.schoolmanagement.data.local.entities.StudentEntity
import com.schoolmanagement.domain.model.Student
import com.schoolmanagement.domain.model.StudentWithInstallments
import com.schoolmanagement.domain.repository.StudentRepository
import com.schoolmanagement.domain.repository.InstallmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine

class StudentRepositoryImpl(
    private val studentDao: StudentDao,
    private val installmentDao: InstallmentDao
) : StudentRepository {

    override fun getAllStudents(): Flow<List<Student>> {
        return studentDao.getAllStudents().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getStudentById(studentId: String): Student? {
        return studentDao.getStudentById(studentId)?.toDomainModel()
    }

    override fun searchStudents(searchQuery: String): Flow<List<Student>> {
        return studentDao.searchStudents(searchQuery).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getStudentsByGrade(grade: String): Flow<List<Student>> {
        return studentDao.getStudentsByGrade(grade).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(student.toEntity())
    }

    override suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student.toEntity())
    }

    override suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student.toEntity())
    }

    override suspend fun getStudentCount(): Int {
        return studentDao.getStudentCount()
    }

    override suspend fun getStudentsWithInstallments(): Flow<List<StudentWithInstallments>> {
        return combine(
            studentDao.getAllStudents(),
            installmentDao.getAllInstallments()
        ) { students, installments ->
            students.map { student ->
                val studentInstallments = installments.filter { it.studentId == student.studentId }
                val totalPaid = studentInstallments.sumOf { it.amountPaid }
                val remaining = student.totalTuition - totalPaid
                
                StudentWithInstallments(
                    student = student.toDomainModel(),
                    installments = studentInstallments.map { it.toDomainModel() },
                    totalPaid = totalPaid,
                    remainingAmount = remaining
                )
            }
        }
    }

    override suspend fun getOverdueStudents(): Flow<List<StudentWithInstallments>> {
        return getStudentsWithInstallments().map { studentsWithInstallments ->
            studentsWithInstallments.filter { it.remainingAmount > 0 }
        }
    }
}

// Extension functions for mapping between entities and domain models
private fun StudentEntity.toDomainModel(): Student {
    return Student(
        studentId = studentId,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        address = address,
        phoneNumber = phoneNumber,
        parentName = parentName,
        parentPhoneNumber = parentPhoneNumber,
        grade = grade,
        enrollmentDate = enrollmentDate,
        totalTuition = totalTuition
    )
}

private fun Student.toEntity(): StudentEntity {
    return StudentEntity(
        studentId = studentId,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        address = address,
        phoneNumber = phoneNumber,
        parentName = parentName,
        parentPhoneNumber = parentPhoneNumber,
        grade = grade,
        enrollmentDate = enrollmentDate,
        totalTuition = totalTuition
    )
}

