package com.example.animon.feature.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UserProfileData(
    val firstName: String = "",
    val secondName: String = "",
    val email: String = "",
    val position: String = "",
    val sector: String = "",
    val phone_number: String = "",
    val date_of_employment: String = "",
    val dangerous_animals_clearance: String = "",
    val skills: List<String> = emptyList()
)

class ProfileViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _profileState = MutableStateFlow<UserProfileData?>(null)
    val profileState: StateFlow<UserProfileData?> = _profileState

    private val _isOwnProfile = MutableStateFlow(false)
    val isOwnProfile: StateFlow<Boolean> = _isOwnProfile

    init {
        val targetUserId: String? = savedStateHandle["userId"]
        val currentUserId = auth.currentUser?.uid
        val finalUserId = targetUserId ?: auth.currentUser?.uid

        _isOwnProfile.value = targetUserId == null || targetUserId == currentUserId

        if (finalUserId != null) {
            loadUserProfile(finalUserId)
        }
    }

    private fun loadUserProfile(userId: String) {
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.toObject(UserProfileData::class.java)
                    _profileState.value = data
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}