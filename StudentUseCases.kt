// Use Cases
package com.schoolmanagement.domain.usecase.student

import com.schoolmanagement.domain.model.Student
import com.schoolmanagement.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow

class GetAllStudentsUseCase(
    private val studentRepository: StudentRepository
) {
    operator fun invoke(): Flow<List<Student>> {
        return studentRepository.getAllStudents()
    }
}

class GetStudentByIdUseCase(
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke(studentId: String): Student? {
        return studentRepository.getStudentById(studentId)
    }
}

class AddStudentUseCase(
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke(student: Student) {
        studentRepository.insertStudent(student)
    }
}

class UpdateStudentUseCase(
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke(student: Student) {
        studentRepository.updateStudent(student)
    }
}

class DeleteStudentUseCase(
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke(student: Student) {
        studentRepository.deleteStudent(student)
    }
}

class SearchStudentsUseCase(
    private val studentRepository: StudentRepository
) {
    operator fun invoke(searchQuery: String): Flow<List<Student>> {
        return studentRepository.searchStudents(searchQuery)
    }
}

class GetOverdueStudentsUseCase(
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke() = studentRepository.getOverdueStudents()
}

