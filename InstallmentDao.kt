// Installment DAO - Data Access Object
package com.schoolmanagement.data.local.dao

import androidx.room.*
import com.schoolmanagement.data.local.entities.InstallmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentDao {
    
    @Query("SELECT * FROM installments ORDER BY paymentDate DESC")
    fun getAllInstallments(): Flow<List<InstallmentEntity>>
    
    @Query("SELECT * FROM installments WHERE studentId = :studentId ORDER BY paymentDate DESC")
    fun getInstallmentsByStudentId(studentId: String): Flow<List<InstallmentEntity>>
    
    @Query("SELECT * FROM installments WHERE installmentId = :installmentId")
    suspend fun getInstallmentById(installmentId: String): InstallmentEntity?
    
    @Query("SELECT SUM(amountPaid) FROM installments WHERE studentId = :studentId")
    suspend fun getTotalPaidByStudent(studentId: String): Double?
    
    @Query("SELECT SUM(amountPaid) FROM installments")
    suspend fun getTotalCollectedAmount(): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallment(installment: InstallmentEntity)
    
    @Update
    suspend fun updateInstallment(installment: InstallmentEntity)
    
    @Delete
    suspend fun deleteInstallment(installment: InstallmentEntity)
    
    @Query("DELETE FROM installments WHERE installmentId = :installmentId")
    suspend fun deleteInstallmentById(installmentId: String)
    
    @Query("SELECT COUNT(*) FROM installments")
    suspend fun getInstallmentCount(): Int
}

