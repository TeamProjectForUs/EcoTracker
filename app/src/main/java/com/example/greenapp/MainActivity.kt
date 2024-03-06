package com.example.greenapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.greenapp.Model.Model
import com.google.android.material.button.MaterialButton


sealed class ActivityMode {
    data object Profile : ActivityMode()
    data object Main : ActivityMode()
}

class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private var navController: NavController? = null
    private var activityMode: ActivityMode = ActivityMode.Main

    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Loader().loadAndSaveAllTips(resources)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.navHostMain) as? NavHostFragment
        navController = navHostFragment?.navController

        val menuButton = findViewById<ImageView>(R.id.menuBtn)
        val backButton = findViewById<ImageView>(R.id.backBtn)
        val editProfileBtn = findViewById<MaterialButton>(R.id.editProfileBtn)

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
        menuButton.setOnClickListener {
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

    }

    fun showMenu() {
        val view = findViewById<ConstraintLayout>(R.id.toolbar)
        view.visibility = VISIBLE
    }

    fun setupProfileMenu() {
        val editProfileBtn = findViewById<MaterialButton>(R.id.editProfileBtn)
        editProfileBtn.visibility = VISIBLE
        activityMode = ActivityMode.Profile

    }

    fun setupNormalMenu() {
        val editProfileBtn = findViewById<MaterialButton>(R.id.editProfileBtn)
        editProfileBtn.visibility = GONE
        activityMode = ActivityMode.Main

    }

    fun hideMenu() {
        val view = findViewById<ConstraintLayout>(R.id.toolbar)
        view.visibility = GONE
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item == null) return false
        return when (item.itemId) {
            android.R.id.home -> {
                navController?.navigateUp()
                true
            }

            R.id.btnProfileFragment -> {
                navController?.navigate(R.id.action_global_profile)
                true
            }

            R.id.btnLogout_menu -> {
                Model.instance.signOut()
                navController?.navigate(R.id.action_global_startFragment)
                true
            }

            R.id.editProfileBtn -> {
                navController?.navigate(R.id.action_global_profileViewFragment)
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

}

