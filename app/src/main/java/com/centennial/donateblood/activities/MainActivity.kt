package com.centennial.donateblood.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.centennial.donateblood.fragments.HomeFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var user: User
    private lateinit var context: Context
    private lateinit var snackbar: Snackbar
    private var activity: Activity = this
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.centennial.donateblood.R.layout.activity_main)
        setSupportActionBar(toolbar)

        mHandler = Handler()
        val fragment = HomeFragment()
        val fragmenttransaction = supportFragmentManager.beginTransaction()
        fragmenttransaction.replace(com.centennial.donateblood.R.id.fragment_container, fragment).commit()
        val drawer = findViewById<DrawerLayout>(com.centennial.donateblood.R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, com.centennial.donateblood.R.string.navigation_drawer_open, com.centennial.donateblood.R.string.navigation_drawer_close
        )
        drawer.setDrawerListener(toggle)
        toggle.syncState()

    }
}
