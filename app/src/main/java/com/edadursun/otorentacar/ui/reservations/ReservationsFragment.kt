package com.edadursun.otorentacar.ui.reservations

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.local.ReservationLocalManager
import com.edadursun.otorentacar.data.repository.ReservationQueryRepository
import com.edadursun.otorentacar.databinding.FragmentReservationsBinding
import com.edadursun.otorentacar.ui.main.MainActivity
import kotlinx.coroutines.launch

// Kullanıcının daha önce oluşturduğu rezervasyonları listeleyen ekran
class ReservationsFragment : Fragment(R.layout.fragment_reservations) {

    private var _binding: FragmentReservationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ReservationsViewModel(
            ReservationQueryRepository(RetrofitProvider.reservationQueryApiService)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReservationsBinding.bind(view)

        setupClicks()
        setupRecycler()
        observeUiState()
    }

    // Ekrana her geri dönüldüğünde kayıtlı rezervasyon kodlarına göre verileri yeniden yükler
    override fun onResume() {
        super.onResume()
        loadReservations()
    }

    // Üst bardaki menü ikonuna basılınca drawer açılır
    private fun setupClicks() {
        binding.topBar.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }
    }

    // RecyclerView için layout manager ayarlanır
    private fun setupRecycler() {
        binding.rvReservations.layoutManager = LinearLayoutManager(requireContext())
    }

    // Lokalde kayıtlı rezervasyon kodlarını alıp backend'den detaylarını çeker
    private fun loadReservations() {
        val savedCodes = ReservationLocalManager.getReservationCodes(requireContext())

        // Hiç kayıtlı rezervasyon yoksa boş liste göster
        if (savedCodes.isEmpty()) {
            binding.rvReservations.adapter = ReservationsAdapter(
                items = emptyList(),
                onDetailClick = {},
                onDeleteClick = {}
            )
            return
        }

        val token = getToken()

        if (token.isBlank()) {
            Log.e("RESERVATIONS_SCREEN", "Rezervasyonlar yüklenemedi çünkü token boş")
            return
        }

        viewModel.loadReservations(
            token = token,
            reservationCodes = savedCodes
        )
    }

    // ViewModel'den gelen state'i dinler ve ekranı günceller
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ReservationsUiState.Idle -> Unit

                    is ReservationsUiState.Loading -> {
                        // İstersen burada loading gösterebilirsin
                    }

                    is ReservationsUiState.Success -> {
                        binding.rvReservations.adapter = ReservationsAdapter(
                            items = state.reservations,
                            onDetailClick = { reservation ->
                                val bundle = Bundle().apply {
                                    putString("reservationCode", reservation.reservationCode)
                                }

                                findNavController().navigate(R.id.bookingDetailFragment, bundle)
                            },
                            onDeleteClick = { reservation ->
                                // Sadece localde tutulan rezervasyon kodunu kaldır
                                ReservationLocalManager.removeReservationCode(requireContext(), reservation.reservationCode)
                                loadReservations()
                            }
                        )
                    }

                    is ReservationsUiState.Error -> {
                        Log.e("RESERVATIONS_SCREEN", "Rezervasyonlar yüklenemedi: ${state.message}")
                    }
                }
            }
        }
    }

    // Uygulama oturumu başlatılırken alınan token TokenStore içinde tutuluyor
    private fun getToken(): String {
        return TokenStore.token.orEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}