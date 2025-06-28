// WhatsApp Service Implementation
package com.schoolmanagement.data.whatsapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.schoolmanagement.data.local.dao.WhatsAppMessageDao
import com.schoolmanagement.data.local.entities.WhatsAppMessageEntity
import com.schoolmanagement.domain.model.WhatsAppMessage
import com.schoolmanagement.domain.repository.WhatsAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WhatsAppServiceImpl(
    private val context: Context,
    private val whatsAppMessageDao: WhatsAppMessageDao
) : WhatsAppRepository {

    override suspend fun sendMessage(phoneNumber: String, message: String): Boolean {
        return try {
            val messageEntity = WhatsAppMessageEntity(
                recipient = "غير محدد",
                phoneNumber = phoneNumber,
                message = message,
                status = "في الانتظار",
                sentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            
            // Save message to database first
            whatsAppMessageDao.insertMessage(messageEntity)
            
            // Check if WhatsApp is installed
            if (!isWhatsAppInstalled()) {
                updateMessageStatus(messageEntity.messageId, "فشل", "واتساب غير مثبت على الجهاز")
                return false
            }
            
            // Send message via WhatsApp Intent
            val success = sendWhatsAppMessage(phoneNumber, message)
            
            if (success) {
                updateMessageStatus(messageEntity.messageId, "مرسل", null)
            } else {
                updateMessageStatus(messageEntity.messageId, "فشل", "فشل في إرسال الرسالة")
            }
            
            success
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun sendBulkMessages(recipients: List<Pair<String, String>>): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        
        recipients.forEach { (phoneNumber, message) ->
            val success = sendMessage(phoneNumber, message)
            results[phoneNumber] = success
        }
        
        return results
    }

    override suspend fun getMessageHistory(): Flow<List<WhatsAppMessage>> {
        return whatsAppMessageDao.getAllMessages().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    private fun isWhatsAppInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            try {
                context.packageManager.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun sendWhatsAppMessage(phoneNumber: String, message: String): Boolean {
        return try {
            // Clean phone number (remove spaces, dashes, etc.)
            val cleanPhoneNumber = phoneNumber.replace(Regex("[^+\\d]"), "")
            
            // Create WhatsApp intent
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhoneNumber&text=${Uri.encode(message)}")
                setPackage("com.whatsapp")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            // Try to start WhatsApp
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Try WhatsApp Business if regular WhatsApp fails
            try {
                val cleanPhoneNumber = phoneNumber.replace(Regex("[^+\\d]"), "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhoneNumber&text=${Uri.encode(message)}")
                    setPackage("com.whatsapp.w4b")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private suspend fun updateMessageStatus(messageId: String, status: String, errorMessage: String?) {
        val message = whatsAppMessageDao.getMessageById(messageId)
        message?.let {
            val updatedMessage = it.copy(
                status = status,
                errorMessage = errorMessage
            )
            whatsAppMessageDao.updateMessage(updatedMessage)
        }
    }

    // Extension function for mapping
    private fun WhatsAppMessageEntity.toDomainModel(): WhatsAppMessage {
        return WhatsAppMessage(
            messageId = messageId,
            recipient = recipient,
            phoneNumber = phoneNumber,
            message = message,
            status = status,
            sentDate = sentDate
        )
    }
}

// WhatsApp Message Templates
object WhatsAppTemplates {
    
    fun getOverduePaymentMessage(studentName: String, parentName: String, amount: Double, dueDate: String): String {
        return """
            السلام عليكم ورحمة الله وبركاته
            
            الأستاذ/ة المحترم/ة $parentName
            
            نود تذكيركم بأن القسط المدرسي للطالب/ة $studentName
            المبلغ: $amount شيكل
            تاريخ الاستحقاق: $dueDate
            
            يرجى المبادرة بسداد المبلغ المستحق في أقرب وقت ممكن.
            
            شكراً لتعاونكم
            إدارة المدرسة
        """.trimIndent()
    }
    
    fun getPaymentConfirmationMessage(studentName: String, parentName: String, amount: Double, paymentDate: String): String {
        return """
            السلام عليكم ورحمة الله وبركاته
            
            الأستاذ/ة المحترم/ة $parentName
            
            نشكركم على سداد القسط المدرسي للطالب/ة $studentName
            المبلغ المدفوع: $amount شيكل
            تاريخ الدفع: $paymentDate
            
            تم تسجيل الدفعة بنجاح في سجلاتنا.
            
            شكراً لتعاونكم
            إدارة المدرسة
        """.trimIndent()
    }
    
    fun getGeneralAnnouncementMessage(title: String, content: String): String {
        return """
            السلام عليكم ورحمة الله وبركاته
            
            $title
            
            $content
            
            مع تحيات إدارة المدرسة
        """.trimIndent()
    }
}

