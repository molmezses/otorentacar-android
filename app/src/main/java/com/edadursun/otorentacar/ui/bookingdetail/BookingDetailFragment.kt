package com.edadursun.otorentacar.ui.bookingdetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentBookingDetailBinding

class BookingDetailFragment : Fragment(R.layout.fragment_booking_detail) {

    private var _binding: FragmentBookingDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingDetailBinding.bind(view)


        setupClicks()
    }



    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}