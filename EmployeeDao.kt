// Employee DAO - Data Access Object
package com.schoolmanagement.data.local.dao

import androidx.room.*
import com.schoolmanagement.data.local.entities.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    
    @Query("SELECT * FROM employees ORDER BY firstName ASC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>
    
    @Query("SELECT * FROM employees WHERE employeeId = :employeeId")
    suspend fun getEmployeeById(employeeId: String): EmployeeEntity?
    
    @Query("SELECT * FROM employees WHERE firstName LIKE '%' || :searchQuery || '%' OR lastName LIKE '%' || :searchQuery || '%'")
    fun searchEmployees(searchQuery: String): Flow<List<EmployeeEntity>>
    
    @Query("SELECT * FROM employees WHERE position = :position")
    fun getEmployeesByPosition(position: String): Flow<List<EmployeeEntity>>
    
    @Query("SELECT SUM(salary) FROM employees")
    suspend fun getTotalSalaries(): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)
    
    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)
    
    @Delete
    suspend fun deleteEmployee(employee: EmployeeEntity)
    
    @Query("DELETE FROM employees WHERE employeeId = :employeeId")
    suspend fun deleteEmployeeById(employeeId: String)
    
    @Query("SELECT COUNT(*) FROM employees")
    suspend fun getEmployeeCount(): Int
}

