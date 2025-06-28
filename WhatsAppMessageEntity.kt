// WhatsApp Message Entity
package com.schoolmanagement.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "whatsapp_messages")
data class WhatsAppMessageEntity(
    @PrimaryKey
    val messageId: String = UUID.randomUUID().toString(),
    val recipient: String,
    val phoneNumber: String,
    val message: String,
    val status: String, // "مرسل", "فشل", "في الانتظار"
    val sentDate: String,
    val errorMessage: String? = null
)

