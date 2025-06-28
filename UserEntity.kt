// User Entity - Room Database
package com.schoolmanagement.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.util.UUID

@Entity(
    tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["employeeId"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class UserEntity(
    @PrimaryKey
    val userId: String = UUID.randomUUID().toString(),
    val username: String,
    val passwordHash: String,
    val role: String, // "مدير" أو "موظف إداري"
    val employeeId: String? = null
)

