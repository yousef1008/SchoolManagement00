// WhatsApp Management Screens
package com.schoolmanagement.presentation.ui.whatsapp

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
import com.schoolmanagement.presentation.viewmodel.WhatsAppViewModel
import com.schoolmanagement.domain.model.WhatsAppMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppScreen(
    viewModel: WhatsAppViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSendMessageDialog by remember { mutableStateOf(false) }
    var showBulkMessageDialog by remember { mutableStateOf(false) }

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
                text = "رسائل واتساب",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showSendMessageDialog = true }
                ) {
                    Text("إرسال رسالة")
                }
                
                Button(
                    onClick = { showBulkMessageDialog = true }
                ) {
                    Text("إرسال جماعي")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "الرسائل المرسلة",
                value = "${uiState.sentCount}",
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
            )
            
            StatCard(
                title = "الرسائل الفاشلة",
                value = "${uiState.failedCount}",
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.errorContainer
            )
            
            StatCard(
                title = "معدل النجاح",
                value = "${uiState.successRate.toInt()}%",
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Messages List
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
                items(uiState.messages) { message ->
                    WhatsAppMessageCard(
                        message = message,
                        onRetryClick = { viewModel.retryFailedMessage(it) }
                    )
                }
            }
        }

        // Dialogs
        if (showSendMessageDialog) {
            SendMessageDialog(
                onDismiss = { showSendMessageDialog = false },
                onSend = { phoneNumber, message ->
                    viewModel.sendSingleMessage(phoneNumber, message)
                    showSendMessageDialog = false
                }
            )
        }

        if (showBulkMessageDialog) {
            BulkMessageDialog(
                onDismiss = { showBulkMessageDialog = false },
                onSend = { recipients, title, content ->
                    viewModel.sendGeneralAnnouncement(recipients, title, content)
                    showBulkMessageDialog = false
                }
            )
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
fun WhatsAppMessageCard(
    message: WhatsAppMessage,
    onRetryClick: (WhatsAppMessage) -> Unit
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
                        text = "المستقبل: ${message.recipient}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "الهاتف: ${message.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "التاريخ: ${message.sentDate}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Status Badge
                val statusColor = when (message.status) {
                    "مرسل" -> MaterialTheme.colorScheme.primary
                    "فشل" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor)
                ) {
                    Text(
                        text = message.status,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "الرسالة: ${message.message.take(100)}${if (message.message.length > 100) "..." else ""}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (message.status == "فشل") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onRetryClick(message) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إعادة الإرسال")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMessageDialog(
    onDismiss: () -> Unit,
    onSend: (String, String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إرسال رسالة واتساب") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("رقم الهاتف") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("نص الرسالة") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(phoneNumber, message) },
                enabled = phoneNumber.isNotBlank() && message.isNotBlank()
            ) {
                Text("إرسال")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkMessageDialog(
    onDismiss: () -> Unit,
    onSend: (List<String>, String, String) -> Unit
) {
    var recipients by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إرسال رسالة جماعية") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = recipients,
                    onValueChange = { recipients = it },
                    label = { Text("أرقام الهواتف (مفصولة بفاصلة)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الرسالة") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("محتوى الرسالة") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val phoneNumbers = recipients.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    onSend(phoneNumbers, title, content) 
                },
                enabled = recipients.isNotBlank() && title.isNotBlank() && content.isNotBlank()
            ) {
                Text("إرسال")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

