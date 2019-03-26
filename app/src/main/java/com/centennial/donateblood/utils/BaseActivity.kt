package com.centennial.donateblood.utils


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.centennial.donateblood.R
import com.centennial.donateblood.activities.LoginActivity
import com.centennial.donateblood.activities.MainActivity
import com.centennial.donateblood.activities.RegistrationActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference

open class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
    }

    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    fun redirectTo(user: FirebaseUser?, dbRef: CollectionReference, activity: AppCompatActivity) {

        if (user != null) {
            Log.i(activity::class.java.simpleName, "Login User:" + user.displayName)
            dbRef.document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.data != null) {
                        hideProgressDialog()
                        Log.d(activity::class.java.simpleName, "DocumentSnapshot data: " + document.data)
                        startActivity(Intent(activity, MainActivity::class.java))

                    } else {
                        hideProgressDialog()
                        Log.d(activity::class.java.simpleName, "No such document")
                        startActivity(Intent(activity, RegistrationActivity::class.java))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(activity::class.java.simpleName, "Data get failed with ", exception)
                    startActivity(Intent(activity, LoginActivity::class.java))
                }

        } else {
            Log.e(activity::class.java.simpleName, "User not logged in")
            startActivity(Intent(activity, LoginActivity::class.java))
        }
        finish()
    }
}