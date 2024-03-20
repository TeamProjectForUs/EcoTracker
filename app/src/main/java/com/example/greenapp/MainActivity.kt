package com.example.greenapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.greenapp.database.Model
import com.example.greenapp.modules.Common.SharedViewModel
import com.google.android.material.button.MaterialButton


sealed class ActivityMode {
    data object Profile : ActivityMode()
    data object Main : ActivityMode()
}

class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private var navController: NavController? = null
    private var activityMode: ActivityMode = ActivityMode.Main
    private lateinit var toolbar: ConstraintLayout


    private lateinit var menuBtn: ImageView
    private lateinit var backButton: ImageView
    private lateinit var editProfileBtn: MaterialButton

    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // uncomment to set first 5 tips to current date
        //Model.instance.postRepository.change5TipsToLatest()
        toolbar = findViewById(R.id.toolbar)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.navHostMain) as? NavHostFragment
        navController = navHostFragment?.navController

        editProfileBtn = findViewById(R.id.editProfileBtn)
        menuBtn = findViewById(R.id.menuBtn)
        backButton = findViewById(R.id.backBtn)
        editProfileBtn = findViewById(R.id.editProfileBtn)

        editProfileBtn.setOnClickListener {
            navController?.navigate(R.id.action_global_editProfile)
        }
        backButton.setOnClickListener {
            if (navController?.currentDestination?.id == R.id.feedFragment) {
                return@setOnClickListener
            }
            if (navController?.currentDestination?.id == R.id.profileFragment) {
                setupNormalMenu()
            }
            back()
        }
        menuBtn.setOnClickListener {
            val menu = PopupMenu(this@MainActivity, it)
            when (activityMode) {
                is ActivityMode.Profile -> {
                    menu.inflate(R.menu.profile_menu)
                }

                is ActivityMode.Main -> {
                    menu.inflate(R.menu.main_menu)
                }
            }
            menu.setOnMenuItemClickListener(this@MainActivity)
            menu.show()
        }

        sharedViewModel.currentUser.observe(this) { user ->
            if (user != null) {
                requestLocationPermissions()
            }
        }

        navController?.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.startFragment) {
                hideMenu()
            } else {
                if (toolbar.visibility == GONE) showMenu()

                if (destination.id == R.id.profileFragment) {
                    showEditProfileBtn()
                } else {
                    hideEditProfileBtn()
                }
                if (destination.id == R.id.registerFragment
                    || destination.id == R.id.loginFragment
                ) {
                    hideMenuOptions()
                } else if (menuBtn.visibility == GONE) showMenuOptions()
                if (destination.id == R.id.feedFragment) {
                    hideBack()
                } else {
                    showBack()
                }
            }
        }

    }

    fun showMenu() {
        toolbar.visibility = VISIBLE
    }

    fun setupProfileMenu() {
        editProfileBtn.visibility = VISIBLE
        activityMode = ActivityMode.Profile
    }

    fun setupNormalMenu() {
        editProfileBtn.visibility = GONE
        activityMode = ActivityMode.Main
    }

    fun hideMenu() {
        toolbar.visibility = GONE
    }

    fun hideMenuOptions() {
        menuBtn.visibility = GONE
        editProfileBtn.visibility = GONE
    }

    fun showMenuOptions() {
        menuBtn.visibility = VISIBLE
        editProfileBtn.visibility = VISIBLE
    }

    fun hideBack() {
        backButton.visibility = GONE
    }

    fun showBack() {
        backButton.visibility = VISIBLE
    }

    fun showEditProfileBtn() {
        editProfileBtn.visibility = VISIBLE
    }

    fun hideEditProfileBtn() {
        editProfileBtn.visibility = GONE
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item == null) return false

        if (item.itemId == R.id.btnLogout_menu) {
            hideMenu()
        } else if (toolbar.visibility == GONE) {
            showMenu()
        }

        return when (item.itemId) {
            android.R.id.home -> {
                navController?.navigateUp()
                true
            }

            R.id.btnProfileFragment -> {
                navController?.navigate(R.id.action_global_profile)
                true
            }


            R.id.btnAirQuality -> {
                navController?.navigate(R.id.action_global_airQaulity)
                true
            }
            R.id.btnLogout_menu -> {
                Model.instance.userRepository.signOut(lifecycleScope)

                navController?.navigate(R.id.action_global_startFragment)
                true
            }

            R.id.btn_ecotips -> {
                navController?.navigate(R.id.action_global_tipsFragment)
                true
            }

            R.id.btn_usersearch -> {
                navController?.navigate(R.id.action_global_searchFragment)
                true
            }
            /* R.id.btnLogoutProfile -> {
                Model.instance.signOut()
                navController?.navigate(R.id.action_global_startFragment)
                true
            }

           R.id.btnMyTips -> {
                navController?.navigate(R.id.action_profileViewFragment_to_myTipsFragment)
                true
            }

            R.id.btnMyGoals -> {
                navController?.navigate(R.id.action_profileViewFragment_to_myGoalsFragment)
                true
            }

            R.id.btnMyPosts -> {
                navController?.navigate(R.id.action_profileViewFragment_to_myPostsFragment)
                true
            }*/

            else -> navController?.let { NavigationUI.onNavDestinationSelected(item, it) }
                ?: super.onOptionsItemSelected(item)
        }
    }

    fun back() {
        navController?.popBackStack()
    }

    fun getSharedViewModel(): SharedViewModel {
        return sharedViewModel
    }

    fun requestLocationPermissions() :Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_DENIED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 101
            )
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101 && resultCode == RESULT_OK) {
            sharedViewModel.getAirQuality(this)
        }
    }

}

