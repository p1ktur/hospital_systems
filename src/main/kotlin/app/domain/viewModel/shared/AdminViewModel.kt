package app.domain.viewModel.shared

import androidx.compose.runtime.*
import app.data.core.*
import app.domain.database.transactor.*
import kotlinx.coroutines.*
import moe.tlaster.precompose.viewmodel.*

class AdminViewModel(
    private val databaseDataExporter: DatabaseDataExporter,
    private val databaseInitializer: DatabaseInitializer
) : ViewModel() {

//    var exportedFilePath = mutableStateOf<String?>(null)

    var isLoading = mutableStateOf(false)

    fun exportData(path: String) {
        if (isLoading.value) return

        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            when (databaseDataExporter.exportDataToAFile(path)) {
//                is TransactorResult.Failure -> {
//                    exportingData = false
//                    exportedFilePath.value = null
//                }
//                is TransactorResult.Success<*> -> {
//                    val path = exportResult.data as String
//
//                    exportingData = false
//                    exportedFilePath.value = path
//                }
                else -> Unit
            }
        }
    }

    fun reInitializeDatabase() {
        if (isLoading.value) return

        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            when (databaseInitializer.deleteInitData()) {
                is TransactorResult.Failure -> isLoading.value = false
                is TransactorResult.Success<*> -> {
                    when (databaseInitializer.initializeDatabaseWithResetOnFailure()) {
                        is TransactorResult.Failure -> Unit
                        is TransactorResult.Success<*> -> Unit
                    }

                    isLoading.value = false
                }
            }
        }
    }
}