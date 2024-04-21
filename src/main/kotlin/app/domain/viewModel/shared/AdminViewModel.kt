package app.domain.viewModel.shared

import app.data.core.*
import kotlinx.coroutines.*
import moe.tlaster.precompose.viewmodel.*

class AdminViewModel(private val databaseDataExporter: DatabaseDataExporter) : ViewModel() {

//    var exportedFilePath = mutableStateOf<String?>(null)

    private var exportingData: Boolean = false

    fun exportData(path: String) {
        if (exportingData) return

        exportingData = true

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
}