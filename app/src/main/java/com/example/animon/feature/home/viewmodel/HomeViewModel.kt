package com.example.animon.feature.home.viewmodel

import androidx.lifecycle.ViewModel
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
    val species: String = ""
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

    init {
        fetchAnimalsFromFirebase()
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
                    val animal = document.toObject(Animal::class.java)
                    animal?.copy(id = document.id)
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

    fun onSectorSelected(sector: String?) {
        _uiState.update { currentState ->
            currentState.copy(selectedSector = sector)
        }
    }
}