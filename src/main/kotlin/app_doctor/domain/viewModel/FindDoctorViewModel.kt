package app_doctor.domain.viewModel

import app_doctor.data.*
import app_doctor.domain.model.*
import app_doctor.domain.uiEvent.*
import app_doctor.domain.uiState.*
import app_shared.domain.model.transactor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class FindDoctorViewModel(private val findDoctorRepository: FindDoctorRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<FindDoctorUiState> = MutableStateFlow(FindDoctorUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchedDoctorData: List<DoctorSearchData> = emptyList()

    fun onUiEvent(event: FindDoctorUiEvent) {
        when (event) {
            is FindDoctorUiEvent.UpdateSearchText -> updateSearchText(event.text)
            FindDoctorUiEvent.Search -> search()
            is FindDoctorUiEvent.Sort -> sort(event.sort)
        }
    }

    private fun updateSearchText(text: String) {
        _uiState.value = uiState.value.copy(
            searchText = text,
            doctorSearchData = fetchedDoctorData.filter {
                (it.name + it.surname + it.login + it.phone + it.age).contains(text, ignoreCase = true)
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun search() {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )

            when (val searchResult = findDoctorRepository.search()) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    fetchedDoctorData = searchResult.data as List<DoctorSearchData>

                    _uiState.value = uiState.value.copy(
                        doctorSearchData = fetchedDoctorData,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sort(sort: FindDoctorSort) {
        _uiState.value = uiState.value.copy(
            doctorSearchData = when (sort) {
                FindDoctorSort.NAME -> uiState.value.doctorSearchData.sortedBy { it.name }
                FindDoctorSort.AGE -> uiState.value.doctorSearchData.sortedBy { it.age }
                FindDoctorSort.POSITION -> uiState.value.doctorSearchData.sortedBy { it.position }
                FindDoctorSort.SALARY -> uiState.value.doctorSearchData.sortedBy { it.salary }
            }
        )
    }
}