package app.domain.viewModel.doctor

import app.data.doctor.*
import app.domain.database.transactor.*
import app.domain.model.doctor.*
import app.domain.uiEvent.doctor.*
import app.domain.uiState.doctor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class DoctorsViewModel(private val doctorsRepository: DoctorsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<DoctorsUiState> = MutableStateFlow(DoctorsUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchedDoctorData: List<DoctorSearchData> = emptyList()
    private var sortAscending = true

    fun onUiEvent(event: DoctorsUiEvent) {
        when (event) {
            is DoctorsUiEvent.UpdateSearchText -> updateSearchText(event.text)
            is DoctorsUiEvent.Search -> search(event.all)
            is DoctorsUiEvent.Sort -> sort(event.sort)
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
    private fun search(all: Boolean) {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )

            when (val searchResult = doctorsRepository.search(all)) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    fetchedDoctorData = searchResult.data as List<DoctorSearchData>

                    _uiState.value = uiState.value.copy(
                        doctorSearchData = when (uiState.value.sort) {
                            DoctorsSort.NAME -> fetchedDoctorData.sortedBy { it.name }
                            DoctorsSort.AGE -> fetchedDoctorData.sortedBy { it.age }
                            DoctorsSort.POSITION -> fetchedDoctorData.sortedBy { it.position }
                            DoctorsSort.SALARY -> fetchedDoctorData.sortedBy { it.salary }
                        },
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sort(sort: DoctorsSort) {
        if (uiState.value.isLoading) return

        if (sort == uiState.value.sort) {
            sortAscending = !sortAscending
        } else {
            _uiState.value = uiState.value.copy(
                sort = sort
            )
        }

        _uiState.value = uiState.value.copy(
            doctorSearchData = if (sortAscending) {
                when (sort) {
                    DoctorsSort.NAME -> uiState.value.doctorSearchData.sortedBy { it.name }
                    DoctorsSort.AGE -> uiState.value.doctorSearchData.sortedBy { it.age }
                    DoctorsSort.POSITION -> uiState.value.doctorSearchData.sortedBy { it.position }
                    DoctorsSort.SALARY -> uiState.value.doctorSearchData.sortedBy { it.salary }
                }
            } else {
                when (sort) {
                    DoctorsSort.NAME -> uiState.value.doctorSearchData.sortedByDescending { it.name }
                    DoctorsSort.AGE -> uiState.value.doctorSearchData.sortedByDescending { it.age }
                    DoctorsSort.POSITION -> uiState.value.doctorSearchData.sortedByDescending { it.position }
                    DoctorsSort.SALARY -> uiState.value.doctorSearchData.sortedByDescending { it.salary }
                }
            }
        )
    }
}