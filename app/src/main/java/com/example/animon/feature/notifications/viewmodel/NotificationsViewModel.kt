package com.example.animon.feature.notifications.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class InAppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val status: String = "",
    val animalId: String = "",
    val timestamp: Long = 0L,
    val formattedDate: String = "",
    val isRead: Boolean = false
)

class NotificationsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _notifications = MutableStateFlow<List<InAppNotification>>(emptyList())
    val notifications: StateFlow<List<InAppNotification>> = _notifications

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("notifications")
            .whereEqualTo("userId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val sdateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

                val list = snapshot.documents.map { doc ->
                    val firebaseTimestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L
                    val formattedStr = if (firebaseTimestamp != 0L) {
                        sdateFormat.format(Date(firebaseTimestamp))
                    } else {
                        ""
                    }

                    InAppNotification(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: "",
                        status = doc.getString("status") ?: "",
                        animalId = doc.getString("animalId") ?: "",
                        timestamp = firebaseTimestamp,
                        formattedDate = formattedStr,
                        isRead = doc.getBoolean("isRead") ?: false
                    )
                }
                _notifications.value = list.sortedByDescending { it.timestamp }
            }
    }

    fun markAsRead(notificationId: String) {
        if (notificationId.isBlank()) return

        db.collection("notifications")
            .document(notificationId)
            .update("isRead", true)
    }

    fun deleteNotification(notificationId: String) {
        if (notificationId.isBlank()) return

        db.collection("notifications")
            .document(notificationId)
            .delete()
    }
}