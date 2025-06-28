// School Database - Room Database
package com.schoolmanagement.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.schoolmanagement.data.local.entities.*
import com.schoolmanagement.data.local.dao.*

@Database(
    entities = [
        StudentEntity::class,
        InstallmentEntity::class,
        EmployeeEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {
    
    abstract fun studentDao(): StudentDao
    abstract fun installmentDao(): InstallmentDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null
        
        fun getDatabase(context: Context): SchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDatabase::class.java,
                    "school_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

