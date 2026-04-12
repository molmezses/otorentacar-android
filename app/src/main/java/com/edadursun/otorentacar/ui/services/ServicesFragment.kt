package com.edadursun.otorentacar.ui.services

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentAboutBinding
import com.edadursun.otorentacar.databinding.FragmentServicesBinding
import com.edadursun.otorentacar.ui.main.MainActivity

class ServicesFragment : Fragment(R.layout.fragment_services) {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentServicesBinding.bind(view)

        setupClicks()
    }

    //Menüye tıklama
    private fun setupClicks() {

        binding.topBar.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}