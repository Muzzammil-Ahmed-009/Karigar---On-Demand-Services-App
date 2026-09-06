package com.karigar.app.data.repository

import com.karigar.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUserId(): String?
    fun getCurrentUserPhone(): String?
    suspend fun saveUserProfile(user: User): Result<Unit>
    suspend fun updateUserProfileWithImage(user: User, newImageUri: android.net.Uri?): Result<Unit>
    suspend fun updateUserName(name: String): Result<Unit>
    suspend fun updateUserRole(role: String): Result<Unit>
    fun getUserProfile(userId: String): Flow<Result<User?>>
    fun getPopularWorkers(): Flow<Result<List<User>>>
    suspend fun signInWithGoogle(idToken: String): Result<String>
    suspend fun logout()
}
