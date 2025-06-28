// Authentication Use Cases
package com.schoolmanagement.domain.usecase.auth

import com.schoolmanagement.domain.model.User
import com.schoolmanagement.domain.repository.UserRepository
import java.security.MessageDigest

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, password: String): User? {
        val hashedPassword = hashPassword(password)
        return userRepository.authenticateUser(username, hashedPassword)
    }
    
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

class RegisterUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Boolean {
        return try {
            val existingUser = userRepository.getUserByUsername(user.username)
            if (existingUser != null) {
                false // User already exists
            } else {
                userRepository.insertUser(user)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): User? {
        return userRepository.getUserById(userId)
    }
}

