// Installment Use Cases
package com.schoolmanagement.domain.usecase.installment

import com.schoolmanagement.domain.model.Installment
import com.schoolmanagement.domain.repository.InstallmentRepository
import kotlinx.coroutines.flow.Flow

class GetAllInstallmentsUseCase(
    private val installmentRepository: InstallmentRepository
) {
    operator fun invoke(): Flow<List<Installment>> {
        return installmentRepository.getAllInstallments()
    }
}

class GetInstallmentsByStudentUseCase(
    private val installmentRepository: InstallmentRepository
) {
    operator fun invoke(studentId: String): Flow<List<Installment>> {
        return installmentRepository.getInstallmentsByStudentId(studentId)
    }
}

class AddInstallmentUseCase(
    private val installmentRepository: InstallmentRepository
) {
    suspend operator fun invoke(installment: Installment) {
        installmentRepository.insertInstallment(installment)
    }
}

class UpdateInstallmentUseCase(
    private val installmentRepository: InstallmentRepository
) {
    suspend operator fun invoke(installment: Installment) {
        installmentRepository.updateInstallment(installment)
    }
}

class DeleteInstallmentUseCase(
    private val installmentRepository: InstallmentRepository
) {
    suspend operator fun invoke(installment: Installment) {
        installmentRepository.deleteInstallment(installment)
    }
}

class GetTotalCollectedAmountUseCase(
    private val installmentRepository: InstallmentRepository
) {
    suspend operator fun invoke(): Double {
        return installmentRepository.getTotalCollectedAmount()
    }
}

class GetTotalPaidByStudentUseCase(
    private val installmentRepository: InstallmentRepository
) {
    suspend operator fun invoke(studentId: String): Double {
        return installmentRepository.getTotalPaidByStudent(studentId)
    }
}

