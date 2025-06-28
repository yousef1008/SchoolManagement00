// Student Management Activities and Fragments
package com.schoolmanagement.presentation.ui.student

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolmanagement.presentation.viewmodel.StudentViewModel
import com.schoolmanagement.domain.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    viewModel: StudentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "إدارة الطلاب",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { /* Navigate to Add Student */ }
            ) {
                Text("إضافة طالب جديد")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModel.searchStudents(it)
            },
            label = { Text("البحث عن طالب...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Students List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.students) { student ->
                    StudentCard(
                        student = student,
                        onEditClick = { viewModel.updateStudent(it) },
                        onDeleteClick = { viewModel.deleteStudent(it) },
                        onViewClick = { /* Navigate to student details */ }
                    )
                }
            }
        }

        // Error handling
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Show snackbar or error dialog
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCard(
    student: Student,
    onEditClick: (Student) -> Unit,
    onDeleteClick: (Student) -> Unit,
    onViewClick: (Student) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "الصف: ${student.grade}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "ولي الأمر: ${student.parentName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "الهاتف: ${student.parentPhoneNumber}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column {
                    Text(
                        text = "القسط الإجمالي",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "₪${student.totalTuition}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onViewClick(student) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("عرض")
                }
                
                OutlinedButton(
                    onClick = { onEditClick(student) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("تعديل")
                }
                
                OutlinedButton(
                    onClick = { onDeleteClick(student) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("حذف")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentScreen(
    student: Student? = null,
    onSave: (Student) -> Unit,
    onCancel: () -> Unit
) {
    var firstName by remember { mutableStateOf(student?.firstName ?: "") }
    var lastName by remember { mutableStateOf(student?.lastName ?: "") }
    var dateOfBirth by remember { mutableStateOf(student?.dateOfBirth ?: "") }
    var address by remember { mutableStateOf(student?.address ?: "") }
    var phoneNumber by remember { mutableStateOf(student?.phoneNumber ?: "") }
    var parentName by remember { mutableStateOf(student?.parentName ?: "") }
    var parentPhoneNumber by remember { mutableStateOf(student?.parentPhoneNumber ?: "") }
    var grade by remember { mutableStateOf(student?.grade ?: "") }
    var totalTuition by remember { mutableStateOf(student?.totalTuition?.toString() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (student == null) "إضافة طالب جديد" else "تعديل بيانات الطالب",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("الاسم الأول") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("الاسم الأخير") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("تاريخ الميلاد") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("العنوان") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("رقم هاتف الطالب") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = parentName,
            onValueChange = { parentName = it },
            label = { Text("اسم ولي الأمر") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = parentPhoneNumber,
            onValueChange = { parentPhoneNumber = it },
            label = { Text("رقم هاتف ولي الأمر") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = grade,
            onValueChange = { grade = it },
            label = { Text("الصف الدراسي") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = totalTuition,
            onValueChange = { totalTuition = it },
            label = { Text("إجمالي القسط") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val newStudent = Student(
                        studentId = student?.studentId ?: java.util.UUID.randomUUID().toString(),
                        firstName = firstName,
                        lastName = lastName,
                        dateOfBirth = dateOfBirth,
                        address = address,
                        phoneNumber = phoneNumber.ifBlank { null },
                        parentName = parentName,
                        parentPhoneNumber = parentPhoneNumber,
                        grade = grade,
                        enrollmentDate = student?.enrollmentDate ?: java.time.LocalDate.now().toString(),
                        totalTuition = totalTuition.toDoubleOrNull() ?: 0.0
                    )
                    onSave(newStudent)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("حفظ")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("إلغاء")
            }
        }
    }
}

