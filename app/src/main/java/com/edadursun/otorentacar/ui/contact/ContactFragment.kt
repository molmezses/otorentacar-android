package com.edadursun.otorentacar.ui.contact

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentContactBinding
import com.edadursun.otorentacar.ui.main.MainActivity

class ContactFragment : Fragment(R.layout.fragment_contact) {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContactBinding.bind(view)

        setupClicks()
    }

    private fun setupClicks() {
        binding.cardWhatsapp.setOnClickListener {
            Toast.makeText(requireContext(), "WhatsApp alanı örnek", Toast.LENGTH_SHORT).show()
        }

        binding.cardPhone.setOnClickListener {
            Toast.makeText(requireContext(), "Telefon alanı örnek", Toast.LENGTH_SHORT).show()
        }

        binding.cardEmail.setOnClickListener {
            Toast.makeText(requireContext(), "E-posta alanı örnek", Toast.LENGTH_SHORT).show()
        }

        binding.cardAddress.setOnClickListener {
            Toast.makeText(requireContext(), "Adres alanı örnek", Toast.LENGTH_SHORT).show()
        }

        binding.cardLocation.setOnClickListener {
            Toast.makeText(requireContext(), "Konum alanı örnek", Toast.LENGTH_SHORT).show()
        }

        binding.topBar.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}