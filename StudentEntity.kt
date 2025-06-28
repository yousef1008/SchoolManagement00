// Student Entity - Room Database
package com.schoolmanagement.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey
    val studentId: String = UUID.randomUUID().toString(),
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
)

