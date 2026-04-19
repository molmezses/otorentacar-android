package com.edadursun.otorentacar.ui.mybookings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentMyBookingsBinding
import com.edadursun.otorentacar.ui.main.MainActivity
import kotlinx.coroutines.launch

class MyBookingsFragment : Fragment(R.layout.fragment_my_bookings) {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyBookingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyBookingsBinding.bind(view)

        setupClicks()
        observeReservationSearch()
    }

    private fun setupClicks() {
        binding.topBar.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        binding.btnFindReservation.setOnClickListener {
            val code = binding.etReservationCode.text?.toString()?.trim().orEmpty()

            if (code.isEmpty()) {
                binding.tvReservationWarning.visibility = View.VISIBLE
                binding.tvReservationWarning.text = "Lütfen rezervasyon kodunu girin."
                return@setOnClickListener
            }

            binding.tvReservationWarning.visibility = View.GONE
            viewModel.searchReservation(code)
        }
    }

    private fun observeReservationSearch() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is MyBookingsUiState.Idle -> {
                        binding.btnFindReservation.isEnabled = true
                        binding.btnFindReservation.text = "Rezervasyonumu Bul"
                    }

                    is MyBookingsUiState.Loading -> {
                        binding.btnFindReservation.isEnabled = false
                        binding.btnFindReservation.text = "Sorgulanıyor..."
                        binding.tvReservationWarning.visibility = View.GONE
                    }

                    is MyBookingsUiState.Success -> {
                        binding.btnFindReservation.isEnabled = true
                        binding.btnFindReservation.text = "Rezervasyonumu Bul"
                        binding.tvReservationWarning.visibility = View.GONE

                        val reservation = state.reservation.`object`

                        val bundle = Bundle().apply {
                            putString("reservationCode", reservation.reservationCode)
                            putString("reservationStatus", reservation.status.name)
                            putString(
                                "vehicleName",
                                "${reservation.vehicleModel.brand.name} ${reservation.vehicleModel.name}"
                            )
                            putString(
                                "vehicleInfo",
                                "${reservation.vehicleModel.transmission.name} | ${reservation.vehicleModel.engine.name}"
                            )
                            putString("pickupDateTime", reservation.pickUpDateTime)
                            putString("dropOffDateTime", reservation.dropOffDateTime)
                            putString("fullName", reservation.fullname)
                            putString("phone", reservation.phone1)
                            putString("birthDate", reservation.birthDate)
                            putString("email", reservation.email)
                            putString("flightCode", reservation.flightNo.orEmpty())
                            putDouble("totalPrice", reservation.totalPrice)
                            putSerializable("extraList", ArrayList(reservation.extraList))

                            // İkinci searchPrices isteği için gerekli alanlar
                            putInt("vehicleModelId", reservation.vehicleModel.modelId)
                            putInt("pickupLocationId", reservation.pickUpLocationPoint.id)
                            putInt("dropOffLocationId", reservation.dropOffLocationPoint.id)
                            putString("rawPickupDateTime", reservation.pickUpDateTime)
                            putString("rawDropOffDateTime", reservation.dropOffDateTime)

                            // searchReservation endpointinde imageList null geldiği için bu şimdilik boş olabilir
                            putString(
                                "vehicleImageUrl",
                                buildImageUrl(reservation.vehicleModel.imageList?.firstOrNull().orEmpty())
                            )

                            android.util.Log.d(
                                "MY_BOOKINGS_IMAGE",
                                "imageList=${reservation.vehicleModel.imageList}"
                            )
                        }

                        findNavController().navigate(
                            R.id.action_myBookingsFragment_to_bookingDetailFragment,
                            bundle
                        )
                    }

                    is MyBookingsUiState.Error -> {
                        binding.btnFindReservation.isEnabled = true
                        binding.btnFindReservation.text = "Rezervasyonumu Bul"
                        binding.tvReservationWarning.visibility = View.VISIBLE
                        binding.tvReservationWarning.text = state.message
                    }
                }
            }
        }
    }

    private fun buildImageUrl(imagePath: String): String {
        if (imagePath.isBlank()) return ""

        return if (imagePath.startsWith("http")) {
            imagePath
        } else {
            val normalizedPath = if (imagePath.startsWith("/")) imagePath else "/$imagePath"
            "https://www.otorentacar.com$normalizedPath"
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}