package com.example.ecotracker;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController

class StartActivity : AppCompatActivity() {


    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

       // val navHostFragment: NavHostFragment? = supportFragmentManager.findFragmentById(R.id.navHostMain) as? NavHostFragment
       // navController = navHostFragment?.navController
       // navController?.let { NavigationUI.setupActionBarWithNavController(this, it) }

       // val bottomNavigationView: BottomNavigationView = findViewById(R.id.mainActivityBottomNavigationView)
        //navController?.let { NavigationUI.setupWithNavController(bottomNavigationView, it) }
//        val btnLogin = findViewById<Button>(R.id.Connectbtn)
//        btnLogin.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        }
//        val btnregister = findViewById<Button>(R.id.Registerbtn)
//        btnregister.setOnClickListener {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
    }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        menuInflater.inflate(R.menu.menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                navController?.navigateUp()
//                true
//            }
//            else -> navController?.let { NavigationUI.onNavDestinationSelected(item, it) } ?: super.onOptionsItemSelected(item)
//        }
//    }
    }

