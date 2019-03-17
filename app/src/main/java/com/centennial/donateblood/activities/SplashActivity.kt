package com.centennial.donateblood.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.centennial.donateblood.R
import com.centennial.donateblood.utils.BaseActivity
import com.centennial.donateblood.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_splash.*

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
            startupRedirect()
        }, SPLASH_TIME_OUT)
    }


    fun startupRedirect() {
        val firebaseUser: FirebaseUser? = auth.currentUser

        if (firebaseUser != null) {
            Log.i(TAG, "Login User:"+firebaseUser.displayName)
            dbRef.document(firebaseUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.data != null) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.data)
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Log.w(TAG, "No such document")
                        startActivity(Intent(this, RegistrationActivity::class.java))
                    }
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed with ", exception)
                    val snackbar = Snackbar
                        .make(frame_splash, "Failure !", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY") { view -> startupRedirect() }.show()
                }

        } else {
            Log.e(TAG, "Not Logged In")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName
        private const val SPLASH_TIME_OUT: Long = 3000
    }

}
