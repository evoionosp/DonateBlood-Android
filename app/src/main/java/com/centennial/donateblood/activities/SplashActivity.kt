package com.centennial.donateblood.activities

import android.os.Bundle
import android.os.Handler
import com.centennial.donateblood.R
import com.centennial.donateblood.utils.BaseActivity
import com.centennial.donateblood.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : BaseActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var userDB: FirebaseFirestore
    private lateinit var dbRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        userDB= FirebaseFirestore.getInstance()
        dbRef = userDB.collection(Constants.USER_DATA_REF)


        Handler().postDelayed({
            redirectTo(auth.currentUser, dbRef, this)
        }, SPLASH_TIME_OUT)
    }



    companion object {
        private val TAG = this::class.java.simpleName
        private const val SPLASH_TIME_OUT: Long = 3000
    }

}
