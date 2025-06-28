// Installment Management Screens
package com.schoolmanagement.presentation.ui.installment

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
import com.schoolmanagement.presentation.viewmodel.InstallmentViewModel
import com.schoolmanagement.domain.model.Installment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentListScreen(
    viewModel: InstallmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                text = "إدارة الأقساط",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { /* Navigate to Add Installment */ }
            ) {
                Text("تسجيل قسط جديد")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "إجمالي المحصل",
                value = "₪${uiState.totalCollected}",
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
            )
            
            StatCard(
                title = "عدد الأقساط",
                value = "${uiState.installments.size}",
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Installments List
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
                items(uiState.installments) { installment ->
                    InstallmentCard(
                        installment = installment,
                        onEditClick = { viewModel.updateInstallment(it) },
                        onDeleteClick = { viewModel.deleteInstallment(it) },
                        onViewClick = { /* Navigate to installment details */ }
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
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentCard(
    installment: Installment,
    onEditClick: (Installment) -> Unit,
    onDeleteClick: (Installment) -> Unit,
    onViewClick: (Installment) -> Unit
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
                        text = "المبلغ: ₪${installment.amountPaid}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "تاريخ الدفع: ${installment.paymentDate}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "طريقة الدفع: ${installment.paymentMethod}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    installment.description?.let { desc ->
                        Text(
                            text = "الوصف: $desc",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onViewClick(installment) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("عرض")
                }
                
                OutlinedButton(
                    onClick = { onEditClick(installment) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("تعديل")
                }
                
                OutlinedButton(
                    onClick = { onDeleteClick(installment) },
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
fun AddEditInstallmentScreen(
    installment: Installment? = null,
    studentId: String? = null,
    onSave: (Installment) -> Unit,
    onCancel: () -> Unit
) {
    var selectedStudentId by remember { mutableStateOf(installment?.studentId ?: studentId ?: "") }
    var amountPaid by remember { mutableStateOf(installment?.amountPaid?.toString() ?: "") }
    var paymentDate by remember { mutableStateOf(installment?.paymentDate ?: LocalDate.now().toString()) }
    var paymentMethod by remember { mutableStateOf(installment?.paymentMethod ?: "نقداً") }
    var description by remember { mutableStateOf(installment?.description ?: "") }

    val paymentMethods = listOf("نقداً", "تحويل بنكي", "شيك", "بطاقة ائتمان")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (installment == null) "تسجيل قسط جديد" else "تعديل القسط",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = selectedStudentId,
            onValueChange = { selectedStudentId = it },
            label = { Text("معرف الطالب") },
            modifier = Modifier.fillMaxWidth(),
            enabled = installment == null // Only allow editing when adding new installment
        )

        OutlinedTextField(
            value = amountPaid,
            onValueChange = { amountPaid = it },
            label = { Text("المبلغ المدفوع") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = paymentDate,
            onValueChange = { paymentDate = it },
            label = { Text("تاريخ الدفع") },
            modifier = Modifier.fillMaxWidth()
        )

        // Payment Method Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = paymentMethod,
                onValueChange = {},
                readOnly = true,
                label = { Text("طريقة الدفع") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                paymentMethods.forEach { method ->
                    DropdownMenuItem(
                        text = { Text(method) },
                        onClick = {
                            paymentMethod = method
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("الوصف (اختياري)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val newInstallment = Installment(
                        installmentId = installment?.installmentId ?: java.util.UUID.randomUUID().toString(),
                        studentId = selectedStudentId,
                        amountPaid = amountPaid.toDoubleOrNull() ?: 0.0,
                        paymentDate = paymentDate,
                        paymentMethod = paymentMethod,
                        description = description.ifBlank { null }
                    )
                    onSave(newInstallment)
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

