package com.edadursun.otorentacar.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentSplashBinding
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    // Splash ekranına ait ViewModel
    private val viewModel: SplashViewModel by viewModels()

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSplashBinding.bind(view)

        // Kullanıcı yeniden dene butonuna basarsa
        // hata alanını gizleyip session başlatmayı tekrar deniyoruz
        binding.btnRetry.setOnClickListener {
            hideError()
            startSession()
        }

        // ViewModel'den gelen state'leri dinlemeye başlıyoruz
        observeUiState()

        // Splash açılır açılmaz token alma/auth işlemini başlatıyoruz
        startSession()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    // Henüz bir işlem başlamamış durum
                    is SplashUiState.Idle -> Unit

                    // Loading state var ama kullanıcıya loading göstermiyoruz
                    is SplashUiState.Loading -> {
                        hideError()
                    }

                    // Token başarıyla alındıysa kullanıcıyı Home ekranına gönderiyoruz
                    is SplashUiState.Success -> {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    }

                    // Hata varsa splash üzerinde hata mesajını gösteriyoruz
                    is SplashUiState.Error -> {
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun startSession() {
        // Fragment artık username/password bilmez
        // sadece ViewModel'e "session başlat" der
        viewModel.initializeSession()
    }

    private fun showError(message: String) {
        binding.errorContainer.visibility = View.VISIBLE
        binding.tvError.text = message
    }

    private fun hideError() {
        binding.errorContainer.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}