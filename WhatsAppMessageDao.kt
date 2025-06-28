// WhatsApp Message DAO
package com.schoolmanagement.data.local.dao

import androidx.room.*
import com.schoolmanagement.data.local.entities.WhatsAppMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WhatsAppMessageDao {
    
    @Query("SELECT * FROM whatsapp_messages ORDER BY sentDate DESC")
    fun getAllMessages(): Flow<List<WhatsAppMessageEntity>>
    
    @Query("SELECT * FROM whatsapp_messages WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): WhatsAppMessageEntity?
    
    @Query("SELECT * FROM whatsapp_messages WHERE phoneNumber = :phoneNumber ORDER BY sentDate DESC")
    fun getMessagesByPhoneNumber(phoneNumber: String): Flow<List<WhatsAppMessageEntity>>
    
    @Query("SELECT * FROM whatsapp_messages WHERE status = :status ORDER BY sentDate DESC")
    fun getMessagesByStatus(status: String): Flow<List<WhatsAppMessageEntity>>
    
    @Query("SELECT COUNT(*) FROM whatsapp_messages WHERE status = 'مرسل'")
    suspend fun getSentMessagesCount(): Int
    
    @Query("SELECT COUNT(*) FROM whatsapp_messages WHERE status = 'فشل'")
    suspend fun getFailedMessagesCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: WhatsAppMessageEntity)
    
    @Update
    suspend fun updateMessage(message: WhatsAppMessageEntity)
    
    @Delete
    suspend fun deleteMessage(message: WhatsAppMessageEntity)
    
    @Query("DELETE FROM whatsapp_messages WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)
    
    @Query("SELECT COUNT(*) FROM whatsapp_messages")
    suspend fun getMessageCount(): Int
}

