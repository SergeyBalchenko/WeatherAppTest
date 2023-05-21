package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.contract.LocationRepository
import com.example.weatherapp.data.contract.NetworkRepository
import com.example.weatherapp.data.contract.WeatherRepository
import com.example.weatherapp.data.error.LocationException
import com.example.weatherapp.data.model.Location
import com.example.weatherapp.mapper.WeatherMapper
import com.example.weatherapp.ui.mvi.ButtonAction
import com.example.weatherapp.ui.mvi.MainError
import com.example.weatherapp.ui.mvi.MainUiEvent
import com.example.weatherapp.ui.mvi.MainUiIntent
import com.example.weatherapp.ui.mvi.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val networkRepository: NetworkRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState.progress())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<MainUiEvent>()
    val events = _events.asSharedFlow()

    fun sendIntent(intent: MainUiIntent) {
        when (intent) {
            is MainUiIntent.GetWeather -> getWeather()
            is MainUiIntent.PermissionsRejected -> handlePermissionsRejected()
            is MainUiIntent.PermissionsDeniedForever -> handlePermissionsDeniedForever()
        }
    }

    private fun handlePermissionsRejected() {
        viewModelScope.launch {
            _state.emit(
                MainUiState.error(
                    MainError(
                        titleRes = R.string.error_title_no_permissions,
                        descriptionRes = R.string.error_description_denied_permissions,
                        buttonAction = ButtonAction.REQUEST_AGAIN
                    )
                )
            )
        }
    }

    private fun handlePermissionsDeniedForever() {
        viewModelScope.launch {
            _state.emit(
                MainUiState.error(
                    MainError(
                        titleRes = R.string.error_title_no_permissions,
                        descriptionRes = R.string.error_description_denied_forever,
                        buttonAction = ButtonAction.OPEN_SETTINGS
                    )
                )
            )
        }
    }

    private fun getWeather() {
        viewModelScope.launch {
            _state.emit(MainUiState.progress())

            if (networkRepository.isNetworkAvailable().not()) {
                _state.emit(
                    MainUiState.error(
                        MainError(
                            titleRes = R.string.error_title_no_network,
                            descriptionRes = R.string.error_description_no_network,
                        )
                    )
                )
                return@launch
            }

            try {
                val location: Location = locationRepository.get()
                val response = weatherRepository.getWeather(location)

                val result = MainUiState.result(WeatherMapper.map(response))
                _state.emit(result)
            } catch (e: IOException) {
                _state.emit(
                    MainUiState.error(
                        MainError(
                            titleRes = R.string.error_title_request_fail,
                            descriptionRes = R.string.error_description_io,
                        )
                    )
                )
            } catch (e: HttpException) {
                _state.emit(
                    MainUiState.error(
                        MainError(
                            titleRes = R.string.error_title_request_fail,
                            descriptionRes = R.string.error_description_http,
                        )
                    )
                )
            } catch (e: LocationException) {
                _state.emit(
                    MainUiState.error(
                        MainError(
                            titleRes = R.string.error_title_location_fail,
                            descriptionRes = R.string.error_description_location_fail,
                        )
                    )
                )
            }
        }
    }
}