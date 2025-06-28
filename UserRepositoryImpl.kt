// User Repository Implementation
package com.schoolmanagement.data.repositories

import com.schoolmanagement.data.local.dao.UserDao
import com.schoolmanagement.data.local.entities.UserEntity
import com.schoolmanagement.domain.model.User
import com.schoolmanagement.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)?.toDomainModel()
    }

    override suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)?.toDomainModel()
    }

    override suspend fun authenticateUser(username: String, password: String): User? {
        val hashedPassword = hashPassword(password)
        return userDao.authenticateUser(username, hashedPassword)?.toDomainModel()
    }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }

    override suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

// Extension functions for mapping
private fun UserEntity.toDomainModel(): User {
    return User(
        userId = userId,
        username = username,
        passwordHash = passwordHash,
        role = role,
        employeeId = employeeId
    )
}

private fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        username = username,
        passwordHash = passwordHash,
        role = role,
        employeeId = employeeId
    )
}

