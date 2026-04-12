package com.edadursun.otorentacar.ui.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentAboutBinding
import com.edadursun.otorentacar.databinding.FragmentContactBinding
import com.edadursun.otorentacar.ui.main.MainActivity

class AboutFragment : Fragment(R.layout.fragment_about) {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)

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
