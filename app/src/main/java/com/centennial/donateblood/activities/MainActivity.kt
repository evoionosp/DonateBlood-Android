package com.centennial.donateblood.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.centennial.donateblood.R
import com.centennial.donateblood.fragments.HomeFragment
import com.centennial.donateblood.fragments.MapViewFragment
import com.centennial.donateblood.utils.BaseActivity
import com.centennial.donateblood.utils.Constants
import com.centennial.donateblood.utils.User
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var context: Context
    private lateinit var snackbar: Snackbar
    private var activity: Activity = this
    private lateinit var mHandler: Handler

    private var firebaseUser: FirebaseUser? = null
    private lateinit var user: User
    private lateinit var auth: FirebaseAuth
    private lateinit var userDB: FirebaseFirestore
    private lateinit var dbRef: CollectionReference

    private lateinit var fragmentTransaction: FragmentTransaction

    private lateinit var headerview: View
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.centennial.donateblood.R.layout.activity_main)
        setSupportActionBar(toolbar)

        navigationView = findViewById<NavigationView>(R.id.navigation_view)
        drawer = findViewById<DrawerLayout>(com.centennial.donateblood.R.id.drawer_layout)

        auth = FirebaseAuth.getInstance()
        userDB= FirebaseFirestore.getInstance()
        dbRef = userDB.collection(Constants.USER_DATA_REF)
        firebaseUser = auth.currentUser
        mHandler = Handler()


        navigationView.setNavigationItemSelectedListener(this)
        headerview = navigationView.getHeaderView(0)
        fragmentTransaction = supportFragmentManager.beginTransaction()



        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, com.centennial.donateblood.R.string.navigation_drawer_open, com.centennial.donateblood.R.string.navigation_drawer_close
        )
        drawer.setDrawerListener(toggle)
        toggle.syncState()


        fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, MapViewFragment()).commit()



    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        title = item.title
        fragmentTransaction.setCustomAnimations(com.centennial.donateblood.R.anim.slide_in_left, com.centennial.donateblood.R.anim.slide_out_right)
        mHandler.postDelayed({
            when(item.itemId){
                com.centennial.donateblood.R.id.menuMap ->
                    fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, MapViewFragment()).commit()
                com.centennial.donateblood.R.id.menuAppointment ->
                    fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, HomeFragment()).commit()
                com.centennial.donateblood.R.id.menuAbout ->
                    fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, HomeFragment()).commit()
            }
        },250)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadUser(): Boolean {

        var status = false
        if (firebaseUser != null) {
            Log.i(TAG, "Login User:"+ firebaseUser!!.displayName)
            dbRef.document(firebaseUser!!.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        hideProgressDialog()
                        Log.d(TAG, "DocumentSnapshot data: " + document.data)
                        user = document.toObject(User::class.java)!!
                        status = true

                    } else {
                        hideProgressDialog()
                        Log.d(TAG, "No such document")
                        startActivity(Intent(this, RegistrationActivity::class.java))
                    }
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }

        } else {
            Log.e(TAG, "User not logged in.")
            startActivity(Intent(this, LoginActivity::class.java))
        }
        return status
    }

    private fun logoutUser(){
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))

        finish()
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(com.centennial.donateblood.R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Exit")
            builder.setMessage("Are you sure you want to Exit ?")

            builder.setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask()
                } else {
                    finish()
                    System.exit(0)
                }
            }
            )
            builder.setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }

            )
            val alert = builder.create()
            alert.show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(com.centennial.donateblood.R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.centennial.donateblood.R.id.menuLogout -> {
                logoutUser()
            }
        }
        return true
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
