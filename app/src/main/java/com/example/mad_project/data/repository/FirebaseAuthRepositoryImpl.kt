package com.karigar.app.data.repository

import com.karigar.app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override fun getCurrentUserPhone(): String? = auth.currentUser?.phoneNumber

    override suspend fun saveUserProfile(user: User): Result<Unit> = try {
        val uid = getCurrentUserId() ?: throw Exception("User not logged in")
        firestore.collection("users").document(uid).set(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateUserProfileWithImage(user: User, newImageUri: android.net.Uri?): Result<Unit> = try {
        val uid = getCurrentUserId() ?: throw Exception("User not logged in")
        
        var updatedProfileImage = user.profileImage

        if (newImageUri != null) {
            // Delete old image if it exists
            if (user.profileImage.isNotEmpty()) {
                try {
                    val oldImageRef = storage.getReferenceFromUrl(user.profileImage)
                    oldImageRef.delete().await()
                } catch (e: Exception) {
                    // Ignore if old image doesn't exist or fails to delete
                }
            }

            // Upload new image
            val newImageRef = storage.reference.child("users/${uid}/profile_pictures/${UUID.randomUUID()}.jpg")
            newImageRef.putFile(newImageUri).await()
            updatedProfileImage = newImageRef.downloadUrl.await().toString()
        }

        val updatedUser = user.copy(profileImage = updatedProfileImage)
        firestore.collection("users").document(uid).set(updatedUser).await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateUserName(name: String): Result<Unit> = try {
        val uid = getCurrentUserId() ?: throw Exception("User not logged in")
        firestore.collection("users").document(uid).update("name", name).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateUserRole(role: String): Result<Unit> = try {
        val uid = getCurrentUserId() ?: throw Exception("User not logged in")
        firestore.collection("users").document(uid).update("currentRole", role).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getUserProfile(userId: String): Flow<Result<User?>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    trySend(Result.success(user))
                } else {
                    trySend(Result.success(null)) // User document doesn't exist yet
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getPopularWorkers(): Flow<Result<List<User>>> = callbackFlow {
        val listener = firestore.collection("users")
            .whereEqualTo("currentRole", "Worker")
            // Optional: .orderBy("rating", Query.Direction.DESCENDING).limit(10) (requires indexing)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val workers = snapshot.toObjects(User::class.java)
                    trySend(Result.success(workers))
                } else {
                    trySend(Result.success(emptyList()))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<String> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()
        Result.success(authResult.user?.uid ?: throw Exception("Login failed"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
