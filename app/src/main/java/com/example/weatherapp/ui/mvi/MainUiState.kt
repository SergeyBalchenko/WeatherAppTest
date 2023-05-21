package com.example.weatherapp.ui.mvi

data class MainUiState (
    val progress: Boolean = true,
    val result: MainResult? = null,
    val error: MainError? = null
) {
    companion object {
        fun progress() = MainUiState(progress = true, result = null, error = null)
        fun result(value: MainResult) = MainUiState(progress = false, result = value, error = null)
        fun error(value: MainError) = MainUiState(progress = false, result = null, error = value)
    }
}

data class MainError(
    val titleRes: Int,
    val descriptionRes: Int,
    val buttonAction: ButtonAction? = null
)

data class MainResult(
    val imageUrl: String?,
    val message: String
)

enum class ButtonAction {
    REQUEST_AGAIN, OPEN_SETTINGS
}

fun MainUiState.isInProgress() = progress && result == null && error == null

fun MainUiState.isAnError() = error != null && result == null && !progress

fun MainUiState.isSuccess() = result != null && error == null && !progress