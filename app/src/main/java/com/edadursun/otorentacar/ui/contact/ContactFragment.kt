package com.edadursun.otorentacar.ui.contact

import android.content.Intent
import android.net.Uri
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
        binding.topBar.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        binding.cardWhatsapp.setOnClickListener {
            openWhatsApp("905317098838")
        }

        binding.cardPhone.setOnClickListener {
            openDialer("+905317098838")
        }

        binding.cardEmail.setOnClickListener {
            openEmail("iletisim@otorentacar.com")
        }

        binding.cardAddress.setOnClickListener {
            openMap("Kayseri Erkilet Havalimanı, Oto Rent A Car Ofis")
        }

        binding.cardLocation.setOnClickListener {
            openMap("Kayseri Erkilet Havalimanı, Oto Rent A Car Ofis")
        }

        binding.tvWhatsapp.setOnClickListener {
            openWhatsApp("905317098838")
        }

        binding.tvPhone.setOnClickListener {
            openDialer("+905317098838")
        }

        binding.tvEmail.setOnClickListener {
            openEmail("iletisim@otorentacar.com")
        }

        binding.tvAddress.setOnClickListener {
            openMap("Kayseri Erkilet Havalimanı, Oto Rent A Car Ofis")
        }
    }

    private fun openWhatsApp(phone: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phone")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp açılamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDialer(phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Arama ekranı açılamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openEmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "E-posta uygulaması açılamadı.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun openMap(address: String) {
        try {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                val fallbackIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}")
                )
                startActivity(fallbackIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Harita açılamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}