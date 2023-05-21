package com.example.weatherapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.mvi.ButtonAction
import com.example.weatherapp.ui.mvi.MainUiEvent
import com.example.weatherapp.ui.mvi.MainUiIntent
import com.example.weatherapp.ui.mvi.MainUiState
import com.example.weatherapp.ui.mvi.isAnError
import com.example.weatherapp.ui.mvi.isInProgress
import com.example.weatherapp.ui.mvi.isSuccess
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private val locationPermissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::checkPermissionsResult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tryAgainButton.setOnClickListener {
            locationPermissionRequestLauncher.launch(REQUIRED_LOCATION_PERMISSIONS)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.onEach(::handleUiState).launchIn(this)
                viewModel.events.onEach(::handleUiEvents).launchIn(this)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (permissionsGranted()) {
            viewModel.sendIntent(MainUiIntent.GetWeather)
        } else {
            locationPermissionRequestLauncher.launch(REQUIRED_LOCATION_PERMISSIONS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun handleUiState(state: MainUiState) {
        if (state.isInProgress()) {
            showLoading()
        } else if (state.isAnError()) {
            val error = requireNotNull(state.error)
            showError(error.buttonAction)
            binding.errorTitle.text = getString(error.titleRes)
            binding.errorDescription.text = getString(error.descriptionRes)
        } else if (state.isSuccess()) {
            showResult()
            val result = requireNotNull(state.result)
            if (result.imageUrl.isNullOrEmpty()) {
                binding.weatherImage.setImageResource(R.drawable.baseline_favorite_border_24)
            } else {
                Picasso.get()
                    .load(result.imageUrl)
                    .placeholder(R.drawable.baseline_favorite_border_24)
                    .into(binding.weatherImage)
            }
            if (result.message.isEmpty()) {
                binding.weatherInfo.text = getString(R.string.mock_temperature_title)
            } else {
                binding.weatherInfo.text = result.message
            }
        }
    }

    private fun handleUiEvents(event: MainUiEvent) {
        // no-op
    }

    private fun checkPermissionsResult(result: Map<String, @JvmSuppressWildcards Boolean>) {
        if (result.all { it.value }) {
            viewModel.sendIntent(MainUiIntent.GetWeather)
        } else {
            if (result.entries.any { shouldShowRequestPermissionRationale(it.key) }) {
                viewModel.sendIntent(MainUiIntent.PermissionsRejected)
            } else {
                viewModel.sendIntent(MainUiIntent.PermissionsDeniedForever)
            }
        }
    }

    private fun permissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    private val openSettingsListener = OnClickListener {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        startActivity(appSettingsIntent)
    }

    private val requestPermissionsListener = OnClickListener {
        locationPermissionRequestLauncher.launch(REQUIRED_LOCATION_PERMISSIONS)
    }

    private fun showError(buttonAction: ButtonAction?) {
        if (buttonAction != null) {
            binding.errorButton.visibility = View.VISIBLE
            binding.tryAgainButton.visibility = View.GONE
            when (buttonAction) {
                ButtonAction.OPEN_SETTINGS -> {
                    binding.errorButton.text = getString(R.string.error_button_open_settings)
                    binding.errorButton.setOnClickListener(openSettingsListener)
                }

                ButtonAction.REQUEST_AGAIN -> {
                    binding.errorButton.text = getString(R.string.error_button_request_permission)
                    binding.errorButton.setOnClickListener(requestPermissionsListener)
                }
            }
        } else {
            binding.tryAgainButton.visibility = View.VISIBLE
            binding.errorButton.visibility = View.GONE
        }

        binding.errorGroup.visibility = View.VISIBLE

        binding.loading.visibility = View.GONE
        binding.weatherGroup.visibility = View.GONE
    }

    private fun showResult() {
        binding.tryAgainButton.visibility = View.VISIBLE
        binding.weatherGroup.visibility = View.VISIBLE

        binding.errorButton.visibility = View.GONE
        binding.loading.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE
    }

    private fun showLoading() {
        binding.loading.visibility = View.VISIBLE

        binding.errorButton.visibility = View.GONE
        binding.tryAgainButton.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE
        binding.weatherGroup.visibility = View.GONE
    }

    companion object {
        private val REQUIRED_LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}