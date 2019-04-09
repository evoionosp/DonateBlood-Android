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
import com.bumptech.glide.Glide
import com.centennial.donateblood.R
import com.centennial.donateblood.fragments.*
import com.centennial.donateblood.models.User
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var context: Context
    private lateinit var snackbar: Snackbar
    private var activity: Activity = this
    private lateinit var mHandler: Handler

    private var firebaseUser: FirebaseUser? = null
    private lateinit var user: User

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


        firebaseUser = auth.currentUser
        mHandler = Handler()


        navigationView.setNavigationItemSelectedListener(this)
        headerview = navigationView.getHeaderView(0)




        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, com.centennial.donateblood.R.string.navigation_drawer_open, com.centennial.donateblood.R.string.navigation_drawer_close
        )
        drawer.setDrawerListener(toggle)
        toggle.syncState()





        loadUser()


        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, DonorMapFragment()).commit()



    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        title = item.title
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(com.centennial.donateblood.R.anim.slide_in_left, com.centennial.donateblood.R.anim.slide_out_right)
        mHandler.postDelayed({
            when(item.itemId){
                com.centennial.donateblood.R.id.menuDonorMap ->
                    fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, DonorMapFragment()).commit()
                com.centennial.donateblood.R.id.menuHospitalMap ->
                    fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, OrgMapFragment()).commit()
                com.centennial.donateblood.R.id.menuRequestList ->
                    fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, RequestListFragment()).commit()
                com.centennial.donateblood.R.id.menuAppointment ->
                    fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, TestingFragment()).commit()
                com.centennial.donateblood.R.id.menuAbout ->
                    fragmentTransaction.replace(com.centennial.donateblood.R.id.fragment_container, AboutUsFragment()).commit()
            }
        },250)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadUser() {

        redirectTo(firebaseUser, userDBRef, this)

        userDBRef.document(firebaseUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {

                    Log.d(activity::class.java.simpleName, "DocumentSnapshot data: " + document.data)

                    user = document.toObject(User::class.java)!!

                    headerview.navUserName.text = user.firstName + " "+user.lastName
                    headerview.navEmail.text = user.emailID

                    Glide
                        .with(this)
                        .load(firebaseUser!!.photoUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_person)
                        .into(headerview.navImage)

                }

                subscribeFCM(user.postalCode.substring(0,3))
                subscribeFCM("BG_"+user.bloodGroup)
            }


        headerview.setOnClickListener {
            onHeaderClick()
        }

    }


    private fun onHeaderClick(){
        var intent = Intent(activity, RegistrationActivity::class.java)
        intent.putExtra("isEdit", false)
        startActivity(intent)
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
            R.id.menuLogout -> {
                logoutUser()
            }
            R.id.menuPP -> {
                startActivity(Intent(activity, RequestDetailsActivity::class.java))
            }
        }
        return true
    }




    companion object {
        private val TAG = this::class.java.simpleName
    }
}
