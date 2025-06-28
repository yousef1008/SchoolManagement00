// Student DAO - Data Access Object
package com.schoolmanagement.data.local.dao

import androidx.room.*
import com.schoolmanagement.data.local.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    
    @Query("SELECT * FROM students ORDER BY firstName ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>
    
    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentById(studentId: String): StudentEntity?
    
    @Query("SELECT * FROM students WHERE firstName LIKE '%' || :searchQuery || '%' OR lastName LIKE '%' || :searchQuery || '%'")
    fun searchStudents(searchQuery: String): Flow<List<StudentEntity>>
    
    @Query("SELECT * FROM students WHERE grade = :grade")
    fun getStudentsByGrade(grade: String): Flow<List<StudentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)
    
    @Update
    suspend fun updateStudent(student: StudentEntity)
    
    @Delete
    suspend fun deleteStudent(student: StudentEntity)
    
    @Query("DELETE FROM students WHERE studentId = :studentId")
    suspend fun deleteStudentById(studentId: String)
    
    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
}

