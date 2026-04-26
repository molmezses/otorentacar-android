package com.edadursun.otorentacar.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.local.FavoritesManager
import com.edadursun.otorentacar.databinding.FragmentFavoritesBinding
import com.edadursun.otorentacar.ui.main.MainActivity

// Favorilere eklenen araçların listelendiği ekran
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    // Fragment görünümü oluşturulduktan sonra çalışan ilk yer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        setupClicks()
        setupRecycler()
    }

    // Ekrana her geri dönüldüğünde favori listesini yeniden yükler
    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    // Tıklama olaylarını burada topluyoruz
    private fun setupClicks() {
        // Üst bardaki menü ikonuna basılınca drawer açılır
        binding.topBar.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }
    }

    // RecyclerView için temel ayarları yapar
    private fun setupRecycler() {
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
    }

    // FavoritesManager içindeki favori araçları alır ve listeye basar
    private fun loadFavorites() {
        val favorites = FavoritesManager.getFavorites()

        binding.rvFavorites.adapter = FavoritesAdapter(favorites) { vehicle ->
            // Kalbe basılınca araç favorilerden çıkarılır
            FavoritesManager.removeFromFavorites(vehicle.id)

            // Listeyi anında yenileyerek ekrana güncel halini basar
            loadFavorites()
        }
    }

    // View yok edilirken binding'i temizleriz
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}