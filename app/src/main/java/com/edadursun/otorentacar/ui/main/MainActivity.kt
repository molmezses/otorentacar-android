package com.edadursun.otorentacar.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Activity'nin view binding referansı
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding ile layout'u bağla
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NavHostFragment ve NavController'ı bul
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Alt custom navigation bar'ı ayarla
        setupCustomBottomBar(navController)

        // Uygulama ilk açıldığında home seçili gelsin
        updateDrawerSelection(R.id.homeFragment)
        updateCustomBottomBarSelection(R.id.homeFragment)

        // Fragment değiştiğinde drawer ve bottom bar seçimini güncelle
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateDrawerSelection(destination.id)
            updateCustomBottomBarSelection(destination.id)
        }

        // Drawer menü tıklamalarını ayarla
        setupDrawerClicks()
    }

    // Alt navigation bar üzerindeki tıklamaları ayarlar
    private fun setupCustomBottomBar(navController: NavController) {
        //Ana sayfa
        binding.navHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.navigate(R.id.homeFragment)
            }
        }

        //Rezervasyonlarım
        binding.navReservations.setOnClickListener {
            if (navController.currentDestination?.id != R.id.reservationsFragment) {
                navController.navigate(R.id.reservationsFragment)
            }
        }

        //Favoriler
        binding.navFavorites.setOnClickListener {
            if (navController.currentDestination?.id != R.id.favoritesFragment) {
                navController.navigate(R.id.favoritesFragment)
            }
        }

        //Sorgula
        binding.navBookings.setOnClickListener {
            if (navController.currentDestination?.id != R.id.myBookingsFragment) {
                navController.navigate(R.id.myBookingsFragment)
            }
        }

        //İletişim
        binding.navContact.setOnClickListener {
            if (navController.currentDestination?.id != R.id.contactFragment) {
                navController.navigate(R.id.contactFragment)
            }
        }
    }

    // Alt navigation bar üzerindeki tüm seçim stillerini sıfırlar
    private fun resetCustomBottomBar() {
        binding.navHomeIndicator.setCardBackgroundColor(getColor(android.R.color.transparent))
        binding.navReservationsIndicator.setCardBackgroundColor(getColor(android.R.color.transparent))
        binding.navFavoritesIndicator.setCardBackgroundColor(getColor(android.R.color.transparent))
        binding.navBookingsIndicator.setCardBackgroundColor(getColor(android.R.color.transparent))
        binding.navContactIndicator.setCardBackgroundColor(getColor(android.R.color.transparent))

        binding.ivNavHome.setColorFilter(getColor(R.color.bottom_nav_inactive))
        binding.ivNavReservations.setColorFilter(getColor(R.color.bottom_nav_inactive))
        binding.ivNavFavorites.setColorFilter(getColor(R.color.bottom_nav_inactive))
        binding.ivNavBookings.setColorFilter(getColor(R.color.bottom_nav_inactive))
        binding.ivNavContact.setColorFilter(getColor(R.color.bottom_nav_inactive))

        binding.tvNavHome.setTextColor(getColor(R.color.bottom_nav_inactive))
        binding.tvNavReservations.setTextColor(getColor(R.color.bottom_nav_inactive))
        binding.tvNavFavorites.setTextColor(getColor(R.color.bottom_nav_inactive))
        binding.tvNavBookings.setTextColor(getColor(R.color.bottom_nav_inactive))
        binding.tvNavContact.setTextColor(getColor(R.color.bottom_nav_inactive))
    }

    // Hangi fragment açıksa alt navigation bar'da onu seçili gösterir
    private fun updateCustomBottomBarSelection(destinationId: Int) {
        resetCustomBottomBar()

        when (destinationId) {
            R.id.homeFragment,
            R.id.aboutFragment,
            R.id.servicesFragment,
            R.id.allVehiclesFragment,
            R.id.extrasFragment,
            R.id.reservationDetailFragment -> {
                binding.navHomeIndicator.setCardBackgroundColor(getColor(R.color.bottom_nav_selected_bg))
                binding.ivNavHome.setColorFilter(getColor(R.color.bottom_nav_active))
                binding.tvNavHome.setTextColor(getColor(R.color.bottom_nav_active))
            }

            R.id.myBookingsFragment,
            R.id.bookingDetailFragment -> {
                binding.navBookingsIndicator.setCardBackgroundColor(getColor(R.color.bottom_nav_selected_bg))
                binding.ivNavBookings.setColorFilter(getColor(R.color.bottom_nav_active))
                binding.tvNavBookings.setTextColor(getColor(R.color.bottom_nav_active))
            }

            R.id.reservationsFragment -> {
                binding.navReservationsIndicator.setCardBackgroundColor(getColor(R.color.bottom_nav_selected_bg))
                binding.ivNavReservations.setColorFilter(getColor(R.color.bottom_nav_active))
                binding.tvNavReservations.setTextColor(getColor(R.color.bottom_nav_active))
            }

            R.id.favoritesFragment -> {
                binding.navFavoritesIndicator.setCardBackgroundColor(getColor(R.color.bottom_nav_selected_bg))
                binding.ivNavFavorites.setColorFilter(getColor(R.color.bottom_nav_active))
                binding.tvNavFavorites.setTextColor(getColor(R.color.bottom_nav_active))
            }

            R.id.contactFragment -> {
                binding.navContactIndicator.setCardBackgroundColor(getColor(R.color.bottom_nav_selected_bg))
                binding.ivNavContact.setColorFilter(getColor(R.color.bottom_nav_active))
                binding.tvNavContact.setTextColor(getColor(R.color.bottom_nav_active))
            }
        }
    }

    // Drawer menüdeki tıklamaları ayarlar
    private fun setupDrawerClicks() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Drawer kapatma butonu
        binding.drawerMenu.ivDrawerClose.setOnClickListener {
            closeDrawer()
        }

        // Home menüsü
        binding.drawerMenu.menuHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.navigate(R.id.homeFragment)
            }
            closeDrawer()
        }

        //Rezervasyonlarım menüsü
        binding.drawerMenu.menuReservations.setOnClickListener {
            if (navController.currentDestination?.id != R.id.reservationsFragment) {
                navController.navigate(R.id.reservationsFragment)
            }
            closeDrawer()
        }

        //Favoriler menüsü
        binding.drawerMenu.menuFavorites.setOnClickListener {
            if (navController.currentDestination?.id != R.id.favoritesFragment) {
                navController.navigate(R.id.favoritesFragment)
            }
            closeDrawer()
        }

        // Rezervasyon Sorgulama menüsü
        binding.drawerMenu.menuBookings.setOnClickListener {
            if (navController.currentDestination?.id != R.id.myBookingsFragment) {
                navController.navigate(R.id.myBookingsFragment)
            }
            closeDrawer()
        }

        // Hakkımızda menüsü
        binding.drawerMenu.menuAbout.setOnClickListener {
            if (navController.currentDestination?.id != R.id.aboutFragment) {
                navController.navigate(R.id.aboutFragment)
            }
            closeDrawer()
        }

        // Hizmetler menüsü
        binding.drawerMenu.menuServices.setOnClickListener {
            if (navController.currentDestination?.id != R.id.servicesFragment) {
                navController.navigate(R.id.servicesFragment)
            }
            closeDrawer()
        }

        // İletişim menüsü
        binding.drawerMenu.menuContact.setOnClickListener {
            if (navController.currentDestination?.id != R.id.contactFragment) {
                navController.navigate(R.id.contactFragment)
            }
            closeDrawer()
        }
    }

    // Drawer menüdeki tüm seçim stillerini sıfırlar
    private fun resetDrawerMenuStyles() {
        val transparent = android.R.color.transparent
        val textColor = getColor(R.color.text_secondary)

        binding.drawerMenu.menuHome.setBackgroundResource(transparent)
        binding.drawerMenu.menuReservations.setBackgroundResource(transparent)
        binding.drawerMenu.menuFavorites.setBackgroundResource(transparent)
        binding.drawerMenu.menuBookings.setBackgroundResource(transparent)
        binding.drawerMenu.menuAbout.setBackgroundResource(transparent)
        binding.drawerMenu.menuServices.setBackgroundResource(transparent)
        binding.drawerMenu.menuContact.setBackgroundResource(transparent)

        binding.drawerMenu.tvMenuHome.setTextColor(textColor)
        binding.drawerMenu.tvMenuReservation.setTextColor(textColor)
        binding.drawerMenu.tvMenuFavorites.setTextColor(textColor)
        binding.drawerMenu.tvMenuBookings.setTextColor(textColor)
        binding.drawerMenu.tvMenuAbout.setTextColor(textColor)
        binding.drawerMenu.tvMenuServices.setTextColor(textColor)
        binding.drawerMenu.tvMenuContact.setTextColor(textColor)

        binding.drawerMenu.ivMenuHome.setColorFilter(textColor)
        binding.drawerMenu.ivMenuReservation.setColorFilter(textColor)
        binding.drawerMenu.ivMenuFavorites.setColorFilter(textColor)
        binding.drawerMenu.ivMenuBookings.setColorFilter(textColor)
        binding.drawerMenu.ivMenuAbout.setColorFilter(textColor)
        binding.drawerMenu.ivMenuServices.setColorFilter(textColor)
        binding.drawerMenu.ivMenuContact.setColorFilter(textColor)
    }

    // Açık olan fragment'e göre drawer menüsünde seçili görünümü uygular
    private fun updateDrawerSelection(selectedId: Int) {
        resetDrawerMenuStyles()

        when (selectedId) {
            R.id.homeFragment -> {
                binding.drawerMenu.menuHome.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuHome.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuHome.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.reservationsFragment -> {
                binding.drawerMenu.menuReservations.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuReservation.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuReservation.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.favoritesFragment -> {
                binding.drawerMenu.menuFavorites.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuFavorites.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuFavorites.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.myBookingsFragment -> {
                binding.drawerMenu.menuBookings.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuBookings.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuBookings.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.aboutFragment -> {
                binding.drawerMenu.menuAbout.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuAbout.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuAbout.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.servicesFragment -> {
                binding.drawerMenu.menuServices.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuServices.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuServices.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.contactFragment -> {
                binding.drawerMenu.menuContact.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuContact.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuContact.setColorFilter(getColor(R.color.primary_green_dark))
            }
        }
    }

    // Drawer'ı açar
    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    // Drawer'ı kapatır
    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    // Geri tuşuna basıldığında drawer açıksa önce onu kapatır
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer()
        } else {
            super.onBackPressed()
        }
    }
}