// Test Cases for School Management App
package com.schoolmanagement.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.schoolmanagement.data.local.SchoolDatabase
import com.schoolmanagement.data.local.entities.StudentEntity
import com.schoolmanagement.data.local.entities.InstallmentEntity
import com.schoolmanagement.data.local.entities.EmployeeEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SchoolDatabaseTest {
    
    private lateinit var database: SchoolDatabase
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, SchoolDatabase::class.java
        ).build()
    }
    
    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }
    
    @Test
    @Throws(Exception::class)
    fun insertAndGetStudent() = runBlocking {
        val studentDao = database.studentDao()
        val student = StudentEntity(
            studentId = "test-student-1",
            firstName = "أحمد",
            lastName = "محمد",
            dateOfBirth = "2010-01-01",
            address = "رام الله",
            phoneNumber = "0599123456",
            parentName = "محمد أحمد",
            parentPhoneNumber = "0599654321",
            grade = "الصف السابع",
            enrollmentDate = "2023-09-01",
            totalTuition = 1000.0
        )
        
        studentDao.insertStudent(student)
        val retrievedStudent = studentDao.getStudentById("test-student-1")
        
        assertNotNull(retrievedStudent)
        assertEquals(student.firstName, retrievedStudent?.firstName)
        assertEquals(student.lastName, retrievedStudent?.lastName)
        assertEquals(student.totalTuition, retrievedStudent?.totalTuition, 0.01)
    }
    
    @Test
    @Throws(Exception::class)
    fun insertAndGetInstallment() = runBlocking {
        val installmentDao = database.installmentDao()
        val studentDao = database.studentDao()
        
        // First insert a student
        val student = StudentEntity(
            studentId = "test-student-2",
            firstName = "فاطمة",
            lastName = "علي",
            dateOfBirth = "2011-05-15",
            address = "نابلس",
            phoneNumber = "0598765432",
            parentName = "علي محمود",
            parentPhoneNumber = "0598123456",
            grade = "الصف السادس",
            enrollmentDate = "2023-09-01",
            totalTuition = 800.0
        )
        studentDao.insertStudent(student)
        
        // Then insert an installment
        val installment = InstallmentEntity(
            installmentId = "test-installment-1",
            studentId = "test-student-2",
            amountPaid = 200.0,
            paymentDate = "2024-01-15",
            paymentMethod = "نقداً",
            description = "قسط الفصل الأول"
        )
        
        installmentDao.insertInstallment(installment)
        val retrievedInstallment = installmentDao.getInstallmentById("test-installment-1")
        
        assertNotNull(retrievedInstallment)
        assertEquals(installment.amountPaid, retrievedInstallment?.amountPaid, 0.01)
        assertEquals(installment.studentId, retrievedInstallment?.studentId)
    }
    
    @Test
    @Throws(Exception::class)
    fun insertAndGetEmployee() = runBlocking {
        val employeeDao = database.employeeDao()
        val employee = EmployeeEntity(
            employeeId = "test-employee-1",
            firstName = "سارة",
            lastName = "خالد",
            position = "معلمة رياضيات",
            phoneNumber = "0597123456",
            address = "الخليل",
            salary = 2500.0,
            hireDate = "2023-08-01"
        )
        
        employeeDao.insertEmployee(employee)
        val retrievedEmployee = employeeDao.getEmployeeById("test-employee-1")
        
        assertNotNull(retrievedEmployee)
        assertEquals(employee.firstName, retrievedEmployee?.firstName)
        assertEquals(employee.position, retrievedEmployee?.position)
        assertEquals(employee.salary, retrievedEmployee?.salary, 0.01)
    }
    
    @Test
    @Throws(Exception::class)
    fun calculateTotalPaidByStudent() = runBlocking {
        val studentDao = database.studentDao()
        val installmentDao = database.installmentDao()
        
        // Insert student
        val student = StudentEntity(
            studentId = "test-student-3",
            firstName = "خالد",
            lastName = "أحمد",
            dateOfBirth = "2009-03-20",
            address = "جنين",
            phoneNumber = "0596789123",
            parentName = "أحمد خالد",
            parentPhoneNumber = "0596456789",
            grade = "الصف الثامن",
            enrollmentDate = "2023-09-01",
            totalTuition = 1200.0
        )
        studentDao.insertStudent(student)
        
        // Insert multiple installments
        val installment1 = InstallmentEntity(
            installmentId = "test-installment-2",
            studentId = "test-student-3",
            amountPaid = 300.0,
            paymentDate = "2024-01-15",
            paymentMethod = "نقداً",
            description = "قسط الفصل الأول"
        )
        
        val installment2 = InstallmentEntity(
            installmentId = "test-installment-3",
            studentId = "test-student-3",
            amountPaid = 400.0,
            paymentDate = "2024-02-15",
            paymentMethod = "تحويل بنكي",
            description = "قسط الفصل الثاني"
        )
        
        installmentDao.insertInstallment(installment1)
        installmentDao.insertInstallment(installment2)
        
        val totalPaid = installmentDao.getTotalPaidByStudent("test-student-3")
        assertEquals(700.0, totalPaid ?: 0.0, 0.01)
    }
    
    @Test
    @Throws(Exception::class)
    fun searchStudentsByName() = runBlocking {
        val studentDao = database.studentDao()
        
        // Insert multiple students
        val students = listOf(
            StudentEntity(
                studentId = "search-test-1",
                firstName = "محمد",
                lastName = "أحمد",
                dateOfBirth = "2010-01-01",
                address = "رام الله",
                phoneNumber = "0599111111",
                parentName = "أحمد محمد",
                parentPhoneNumber = "0599222222",
                grade = "الصف السابع",
                enrollmentDate = "2023-09-01",
                totalTuition = 1000.0
            ),
            StudentEntity(
                studentId = "search-test-2",
                firstName = "أحمد",
                lastName = "علي",
                dateOfBirth = "2010-02-01",
                address = "نابلس",
                phoneNumber = "0599333333",
                parentName = "علي أحمد",
                parentPhoneNumber = "0599444444",
                grade = "الصف السابع",
                enrollmentDate = "2023-09-01",
                totalTuition = 1000.0
            ),
            StudentEntity(
                studentId = "search-test-3",
                firstName = "فاطمة",
                lastName = "خالد",
                dateOfBirth = "2010-03-01",
                address = "الخليل",
                phoneNumber = "0599555555",
                parentName = "خالد محمود",
                parentPhoneNumber = "0599666666",
                grade = "الصف السابع",
                enrollmentDate = "2023-09-01",
                totalTuition = 1000.0
            )
        )
        
        students.forEach { studentDao.insertStudent(it) }
        
        // Search for students with "أحمد" in their name
        studentDao.searchStudents("أحمد").collect { results ->
            assertEquals(2, results.size)
            assertTrue(results.any { it.firstName == "محمد" && it.lastName == "أحمد" })
            assertTrue(results.any { it.firstName == "أحمد" && it.lastName == "علي" })
        }
    }
}

