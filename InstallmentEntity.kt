// Installment Entity - Room Database
package com.schoolmanagement.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.util.UUID

@Entity(
    tableName = "installments",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["studentId"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InstallmentEntity(
    @PrimaryKey
    val installmentId: String = UUID.randomUUID().toString(),
    val studentId: String,
    val amountPaid: Double,
    val paymentDate: String,
    val paymentMethod: String,
    val description: String?
)

