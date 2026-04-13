package com.edadursun.otorentacar.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edadursun.otorentacar.R
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.edadursun.otorentacar.core.config.AppConfig

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SESSION BAŞLATIR USERNAME/PASSWORD BİLMEZ

        viewModel.initializeSession(
            username = AppConfig.USERNAME,
            password = AppConfig.PASSWORD
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is SplashUiState.Idle -> Unit

                    is SplashUiState.Loading -> {
                        Log.d("AUTH_TEST", "Loading")
                    }

                    is SplashUiState.Success -> {
                        Log.d("AUTH_TEST", "Session initialized successfully")
                    }

                    is SplashUiState.Error -> {
                        Log.e("AUTH_TEST", "Error: ${state.message}")
                    }
                }
            }
        }
    }
}