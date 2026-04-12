package com.edadursun.otorentacar.ui.mybookings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentMyBookingsBinding
import com.edadursun.otorentacar.ui.main.MainActivity

class MyBookingsFragment : Fragment(R.layout.fragment_my_bookings) {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!

    private val validCodes = listOf("ABC-12345", "OTO-2024", "TEST-001")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyBookingsBinding.bind(view)

        setupClicks()
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
            binding.btnFindReservation.text = "Sorgulanıyor.."
            binding.btnFindReservation.isEnabled = false

            Handler(Looper.getMainLooper()).postDelayed({
                if (validCodes.contains(code.uppercase())) {
                    binding.btnFindReservation.text = "Rezervasyonumu Bul"
                    binding.btnFindReservation.isEnabled = true

                    findNavController().navigate(R.id.bookingDetailFragment)
                } else {
                    binding.btnFindReservation.text = "Rezervasyonumu Bul"
                    binding.btnFindReservation.isEnabled = true
                    binding.tvReservationWarning.visibility = View.VISIBLE
                    binding.tvReservationWarning.text = "Rezervasyon kodu bulunamadı."
                }
            }, 1500)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}