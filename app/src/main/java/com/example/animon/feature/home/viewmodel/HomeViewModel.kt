package com.example.animon.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import com.example.animon.feature.details.viewmodel.AnimalNorms
import com.example.animon.feature.details.viewmodel.AnimalStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Animal(
    val id: String = "",
    val name: String = "",
    val photo: String = "",
    val location: String = "",
    val hasImage: Boolean = false,
    val species: String = "",
    val weight: String = "",
    val temperature: String = "",
    val pulse: String = "",
    val appetite: String = "",
    val rabies_vaccination: String = "",
    val date_of_birth: String = "",
    val gender: String = "",
    val size: String = "",
    val calculated_status: String = "",
    var status: AnimalStatus = AnimalStatus.UNKNOWN
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val animals: List<Animal> = emptyList(),
    val selectedSector: String? = null,
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var normsMap: Map<String, AnimalNorms> = emptyMap()

    init {
        fetchNormsAndThenAnimals()
    }

    private fun fetchNormsAndThenAnimals() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        db.collection("animal_norms").get().addOnSuccessListener { snapshot ->
            val map = mutableMapOf<String, AnimalNorms>()
            for (doc in snapshot.documents) {
                doc.toObject(AnimalNorms::class.java)?.let { norm ->
                    map[doc.id] = norm
                }
            }
            normsMap = map
            fetchAnimalsFromFirebase()
        }.addOnFailureListener {
            fetchAnimalsFromFirebase()
        }
    }

    private fun fetchAnimalsFromFirebase() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        db.collection("animals").addSnapshotListener { snapshot, error ->
            if (error != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Błąd pobierania danych: ${error.message}"
                    )
                }
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val fetchedAnimals = snapshot.documents.mapNotNull { document ->
                    val animal = document.toObject(Animal::class.java)?.copy(id = document.id)
                    animal?.apply {
                        this.status = if (this.calculated_status.isNotBlank()) {
                            try {
                                AnimalStatus.valueOf(this.calculated_status)
                            } catch (e: Exception) {
                                AnimalStatus.UNKNOWN
                            }
                        } else {
                            calculateStatusForList(this, normsMap)
                        }
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        animals = fetchedAnimals
                    )
                }
            }
        }
    }

    private fun calculateStatusForList(animal: Animal, normsMap: Map<String, AnimalNorms>): AnimalStatus {
        val norms = normsMap[animal.species.lowercase().trim()] ?: return AnimalStatus.UNKNOWN

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

    fun onSectorSelected(sector: String?) {
        _uiState.update { currentState ->
            currentState.copy(selectedSector = sector)
        }
    }

    fun addAnimal(name: String, species: String, location: String, dateoOfBirth: String, gender: String, size: String) {
        val newAnimal = Animal(
            name = name.trim(),
            species = species.trim(),
            location = location.trim(),
            date_of_birth = dateoOfBirth.trim(),
            gender = gender.trim(),
            size = size.trim()
        )

        db.collection("animals").add(newAnimal)
    }
}