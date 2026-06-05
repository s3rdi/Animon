package com.example.animon.feature.details.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class AnimalStatus(val label: String, val icon: ImageVector, val color: Color) {
    GOOD("Dobry", Icons.Default.CheckCircle, Color(0xFF4CAF50)),
    WARNING("Obserwacja", Icons.Default.Warning, Color(0xFFFFC107)),
    CRITICAL("Zagrożenie", Icons.Default.Error, Color(0xFFF44336)),
    UNKNOWN("Brak danych", Icons.AutoMirrored.Filled.Help, Color.Gray)
}

data class AnimalData(
    val name: String = "",
    val photo: String = "",
    val location: String = "",
    val weight: String = "",
    val date_of_birth: String = "",
    val gender: String = "",
    val species: String = "",
    val size: String = "",
    val castration: String = "",
    val temperature: String = "",
    val pulse: String = "",
    val appetite: String = "",
    val rabies_vaccination: String = ""
)

data class AnimalNorms(
    val pulse_max: Int = 0,
    val pulse_min: Int = 0,
    val temp_max: Double = 0.0,
    val temp_min: Double = 0.0,
    val weight_max: Int = 0,
    val weight_min: Int = 0
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
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val animalId: String? = savedStateHandle["animalId"]

    private val _isVeterinarian = MutableStateFlow(false)
    val isVeterinarian: StateFlow<Boolean> = _isVeterinarian

    private val _currentVetName = MutableStateFlow("")

    private val _normsState = MutableStateFlow<AnimalNorms?>(null)

    private val _animalState = MutableStateFlow<AnimalData?>(null)
    val animalState: StateFlow<AnimalData?> = _animalState

    private val _medicalRecordsState = MutableStateFlow<List<MedicalRecord>>(emptyList())
    val medicalRecordsState: StateFlow<List<MedicalRecord>> = _medicalRecordsState

    private val _passportState = MutableStateFlow<List<PassportSection>>(emptyList())
    val passportState: StateFlow<List<PassportSection>> = _passportState

    val calculatedStatusState: StateFlow<AnimalStatus> = combine(_animalState, _normsState) { animal, norms ->
        if (animal == null || norms == null) return@combine AnimalStatus.UNKNOWN

        calculateHeuristicStatus(animal, norms)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnimalStatus.UNKNOWN
    )

    init {
        if (animalId != null) {
            loadAnimalDetails(animalId)
            checkUserRole()
        }
    }

    private fun loadAnimalDetails(animalId: String) {
        db.collection("animals").document(animalId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.toObject(AnimalData::class.java)
                    _animalState.value = data

                    data?.species?.let { speciesName ->
                        loadAnimalNorms(speciesName.lowercase().trim())
                    }
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

    private fun loadAnimalNorms(species: String) {
        db.collection("animal_norms").document(species)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val norms = document.toObject(AnimalNorms::class.java)
                    _normsState.value = norms
                }
            }
    }


    private fun calculateHeuristicStatus(animal: AnimalData, norms: AnimalNorms): AnimalStatus {
        val tempVal = animal.temperature.replace(",", ".").toDoubleOrNull()
        val pulseVal = animal.pulse.toIntOrNull()
        val weightVal = animal.weight.toIntOrNull()

        if (tempVal == null && pulseVal == null && weightVal == null) return AnimalStatus.UNKNOWN

        var penaltyPoints = 0.0

        if (tempVal != null) {
            when {
                tempVal < (norms.temp_min - 0.5) || tempVal > (norms.temp_max + 0.7) -> penaltyPoints += 0.5
                tempVal < norms.temp_min || tempVal > norms.temp_max -> penaltyPoints += 0.2
            }
        }

        if (pulseVal != null) {
            when {
                pulseVal < (norms.pulse_min - 10) || pulseVal > (norms.pulse_max + 20) -> penaltyPoints += 0.4
                pulseVal < norms.pulse_min || pulseVal > norms.pulse_max -> penaltyPoints += 0.15
            }
        }

        if (weightVal != null) {
            when {
                weightVal < (norms.weight_min - 10) || weightVal > (norms.weight_max + 20) -> penaltyPoints += 0.4
                weightVal < norms.weight_min || weightVal > norms.weight_max -> penaltyPoints += 0.15
            }
        }

        when (animal.appetite.lowercase().trim()) {
            "nie" -> penaltyPoints += 0.5
        }

        when (animal.rabies_vaccination.lowercase().trim()) {
            "nie" -> penaltyPoints += 0.2
        }

        return when {
            penaltyPoints >= 1 -> AnimalStatus.CRITICAL
            penaltyPoints >= 0.2 -> AnimalStatus.WARNING
            else -> AnimalStatus.GOOD
        }
    }

    fun addMedicalRecord(title: String, description: String, date: String) {
        val id = animalId ?: return
        val currentUserId = auth.currentUser?.uid ?: ""

        val newRecord = hashMapOf(
            "title" to title,
            "description" to description,
            "date" to date,
            "vetId" to currentUserId,
            "vetName" to _currentVetName.value
        )

        db.collection("animals")
            .document(id)
            .collection("medical_records")
            .add(newRecord)
    }

    fun deleteMedicalRecord(recordId: String) {
        val id = animalId ?: return

        db.collection("animals")
            .document(id)
            .collection("medical_records")
            .document(recordId)
            .delete()
    }

    fun updateMedicalRecord(recordId: String, updatedTitle: String, updatedDescription: String) {
        val id = animalId ?: return

        db.collection("animals")
            .document(id)
            .collection("medical_records")
            .document(recordId)
            .update(
                mapOf(
                    "title" to updatedTitle,
                    "description" to updatedDescription
                )
            )
    }

    fun updateAnimalDocument(updatedAnimalData: AnimalData) {
        val id = animalId ?: return

        db.collection("animals")
            .document(id)
            .set(updatedAnimalData)
    }

    private fun checkUserRole() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("position")
                    val firstName = document.getString("first_name")
                    val secondName = document.getString("second_name")

                    if (role == "Weterynarz") {
                        _isVeterinarian.value = true
                        _currentVetName.value = "lek. wet. $firstName $secondName"
                    }
                }
            }
    }

    fun isCurrentUserAuthor(vetId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false
        return currentUserId == vetId
    }
}