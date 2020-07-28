
package com.harshit.imagesearch.activity

import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.harshit.imagesearch.R
import com.harshit.imagesearch.fragment.FAQsFragment

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        mAuth = FirebaseAuth.getInstance()

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            openHome()
        }

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> openHome()
            R.id.nav_faqs -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    FAQsFragment()
                ).commit()
                supportActionBar!!.setTitle("FAQs")
            }
            R.id.nav_logout -> AlertDialog.Builder(this@DashboardActivity)
                .setTitle("Logout!")
                .setMessage("Are you sure you want to Logout?")
                .setCancelable(true)
                .setNegativeButton(
                    "No"
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                    finish()
                }.show()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openHome() {
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.nav_home)
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            HomeFragment()
        ).commit()
        supportActionBar!!.setTitle("Home")
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            val fragment = this.supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment is HomeFragment) {
                finish()
            } else {
                openHome()
            }
        }
    }

    override fun onResume() {
        isOnline()
        val user = mAuth.currentUser
        if (user == null) {
            startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
        }
        super.onResume()
    }

    fun isOnline(): Boolean {
        val conMgr =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            AlertDialog.Builder(this@DashboardActivity)
                .setTitle("No Internet Connection!")
                .setMessage("Please Connect to Internet..")
                .setCancelable(true)
                .setPositiveButton(
                    "Ok"
                ) { dialog, which -> this@DashboardActivity.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }
                .show()
            return false
        }
        return true
    }

}