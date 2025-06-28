// Installment Repository Implementation
package com.schoolmanagement.data.repositories

import com.schoolmanagement.data.local.dao.InstallmentDao
import com.schoolmanagement.data.local.entities.InstallmentEntity
import com.schoolmanagement.domain.model.Installment
import com.schoolmanagement.domain.repository.InstallmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InstallmentRepositoryImpl(
    private val installmentDao: InstallmentDao
) : InstallmentRepository {

    override fun getAllInstallments(): Flow<List<Installment>> {
        return installmentDao.getAllInstallments().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getInstallmentsByStudentId(studentId: String): Flow<List<Installment>> {
        return installmentDao.getInstallmentsByStudentId(studentId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getInstallmentById(installmentId: String): Installment? {
        return installmentDao.getInstallmentById(installmentId)?.toDomainModel()
    }

    override suspend fun getTotalPaidByStudent(studentId: String): Double {
        return installmentDao.getTotalPaidByStudent(studentId) ?: 0.0
    }

    override suspend fun getTotalCollectedAmount(): Double {
        return installmentDao.getTotalCollectedAmount() ?: 0.0
    }

    override suspend fun insertInstallment(installment: Installment) {
        installmentDao.insertInstallment(installment.toEntity())
    }

    override suspend fun updateInstallment(installment: Installment) {
        installmentDao.updateInstallment(installment.toEntity())
    }

    override suspend fun deleteInstallment(installment: Installment) {
        installmentDao.deleteInstallment(installment.toEntity())
    }

    override suspend fun getInstallmentCount(): Int {
        return installmentDao.getInstallmentCount()
    }
}

// Extension functions for mapping
private fun InstallmentEntity.toDomainModel(): Installment {
    return Installment(
        installmentId = installmentId,
        studentId = studentId,
        amountPaid = amountPaid,
        paymentDate = paymentDate,
        paymentMethod = paymentMethod,
        description = description
    )
}

private fun Installment.toEntity(): InstallmentEntity {
    return InstallmentEntity(
        installmentId = installmentId,
        studentId = studentId,
        amountPaid = amountPaid,
        paymentDate = paymentDate,
        paymentMethod = paymentMethod,
        description = description
    )
}

