package com.edadursun.otorentacar.ui.reservationsuccess

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentReservationSuccessBinding

class ReservationSuccessFragment : Fragment(R.layout.fragment_reservation_success) {

    private var _binding: FragmentReservationSuccessBinding? = null
    private val binding get() = _binding!!

    private var reservationCode: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReservationSuccessBinding.bind(view)

        reservationCode = arguments?.getString("reservationCode").orEmpty()
        binding.tvReservationCode.text = reservationCode

        binding.btnCopyCode.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("reservation_code", reservationCode)
            clipboard.setPrimaryClip(clip)
        }

        binding.btnDone.setOnClickListener {
            findNavController().navigate(R.id.action_reservationSuccessFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}