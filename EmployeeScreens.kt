// Employee Management Screens
package com.schoolmanagement.presentation.ui.employee

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
import com.schoolmanagement.presentation.viewmodel.EmployeeViewModel
import com.schoolmanagement.domain.model.Employee
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    viewModel: EmployeeViewModel = viewModel()
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
                text = "إدارة الموظفين",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { /* Navigate to Add Employee */ }
            ) {
                Text("إضافة موظف جديد")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "إجمالي الموظفين",
                value = "${uiState.employeeCount}",
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
            )
            
            StatCard(
                title = "إجمالي الرواتب",
                value = "₪${uiState.totalSalaries}",
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
            )
            
            StatCard(
                title = "متوسط الراتب",
                value = if (uiState.employeeCount > 0) "₪${(uiState.totalSalaries / uiState.employeeCount).toInt()}" else "₪0",
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModel.searchEmployees(it)
            },
            label = { Text("البحث عن موظف...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Employees List
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
                items(uiState.employees) { employee ->
                    EmployeeCard(
                        employee = employee,
                        onEditClick = { viewModel.updateEmployee(it) },
                        onDeleteClick = { viewModel.deleteEmployee(it) },
                        onViewClick = { /* Navigate to employee details */ }
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

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    backgroundColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeCard(
    employee: Employee,
    onEditClick: (Employee) -> Unit,
    onDeleteClick: (Employee) -> Unit,
    onViewClick: (Employee) -> Unit
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
                        text = employee.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "المسمى: ${employee.position}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "الهاتف: ${employee.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "تاريخ التوظيف: ${employee.hireDate}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "الراتب الشهري",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "₪${employee.salary}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onViewClick(employee) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("عرض")
                }
                
                OutlinedButton(
                    onClick = { onEditClick(employee) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("تعديل")
                }
                
                OutlinedButton(
                    onClick = { onDeleteClick(employee) },
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
fun AddEditEmployeeScreen(
    employee: Employee? = null,
    onSave: (Employee) -> Unit,
    onCancel: () -> Unit
) {
    var firstName by remember { mutableStateOf(employee?.firstName ?: "") }
    var lastName by remember { mutableStateOf(employee?.lastName ?: "") }
    var position by remember { mutableStateOf(employee?.position ?: "") }
    var phoneNumber by remember { mutableStateOf(employee?.phoneNumber ?: "") }
    var address by remember { mutableStateOf(employee?.address ?: "") }
    var salary by remember { mutableStateOf(employee?.salary?.toString() ?: "") }
    var hireDate by remember { mutableStateOf(employee?.hireDate ?: LocalDate.now().toString()) }

    val positions = listOf(
        "معلم رياضيات", "معلم لغة عربية", "معلم علوم", "معلم تاريخ", 
        "معلم جغرافيا", "معلم لغة إنجليزية", "معلم تربية إسلامية",
        "مدير إداري", "محاسب", "سكرتير", "عامل نظافة", "حارس أمن"
    )
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (employee == null) "إضافة موظف جديد" else "تعديل بيانات الموظف",
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

        // Position Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("المسمى الوظيفي") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                positions.forEach { pos ->
                    DropdownMenuItem(
                        text = { Text(pos) },
                        onClick = {
                            position = pos
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("رقم الهاتف") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("العنوان") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = salary,
            onValueChange = { salary = it },
            label = { Text("الراتب الشهري") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = hireDate,
            onValueChange = { hireDate = it },
            label = { Text("تاريخ التوظيف") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val newEmployee = Employee(
                        employeeId = employee?.employeeId ?: java.util.UUID.randomUUID().toString(),
                        firstName = firstName,
                        lastName = lastName,
                        position = position,
                        phoneNumber = phoneNumber,
                        address = address,
                        salary = salary.toDoubleOrNull() ?: 0.0,
                        hireDate = hireDate
                    )
                    onSave(newEmployee)
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

