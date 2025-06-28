// Employee Entity - Room Database
package com.schoolmanagement.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey
    val employeeId: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val position: String,
    val phoneNumber: String,
    val address: String,
    val salary: Double,
    val hireDate: String
)

