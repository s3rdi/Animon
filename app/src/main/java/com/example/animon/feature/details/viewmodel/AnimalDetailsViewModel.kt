package com.example.animon.feature.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AnimalData(
    val name: String = "",
    val location: String = "",
    val weight: String = "",
    val age: String = "",
    val gender: String = "",
    val species: String = "",
    val size: String = "",
    val castration: String = "",
)

data class MedicalRecord(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val description: String = "",
    val vetId: String = "",
    val vetName: String = ""
)

data class PassportSection(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val items: Map<String, String> = emptyMap()
)
class AnimalDetailsViewModel (savedStateHandle: SavedStateHandle) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _animalState = MutableStateFlow<AnimalData?>(null)
    val animalState: StateFlow<AnimalData?> = _animalState

    private val _medicalRecordsState = MutableStateFlow<List<MedicalRecord>>(emptyList())
    val medicalRecordsState: StateFlow<List<MedicalRecord>> = _medicalRecordsState

    private val _passportState = MutableStateFlow<List<PassportSection>>(emptyList())
    val passportState: StateFlow<List<PassportSection>> = _passportState

    init {
        val animalId: String? = savedStateHandle["animalId"]
        if (animalId != null) {
            loadAnimalDetails(animalId)
        }
    }

    private fun loadAnimalDetails(animalId: String) {
        db.collection("animals").document(animalId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.toObject(AnimalData::class.java)
                    _animalState.value = data
                }
            }

        db.collection("animals").document(animalId).collection("medical_records")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val records = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MedicalRecord::class.java)?.copy(id = doc.id)
                    }
                    _medicalRecordsState.value = records
                }
            }

        db.collection("animals").document(animalId).collection("passport")
            .orderBy("order")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val records = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PassportSection::class.java)?.copy(id = doc.id)
                    }
                    _passportState.value = records
                }
            }
    }
}