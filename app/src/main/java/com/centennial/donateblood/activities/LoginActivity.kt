package com.centennial.donateblood.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.centennial.donateblood.R
import com.centennial.donateblood.extensions.IsNullOrEmpty
import com.fxn.stash.Stash
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.android.synthetic.main.activity_login.*


/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
class LoginActivity : BaseActivity(), View.OnClickListener {

    // [START declare_auth]


    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        title = getString(R.string.login)
        Stash.init(this)
        modifyGoogleButton()
        // Button listeners
        btnLoginWithGoogle.setOnClickListener(this)
        btnLoginWithEmail.setOnClickListener(this)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)




        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener(this) {

                    verifySignInLink(intent)
            }
            .addOnFailureListener(this) { e ->
                Toast.makeText(this,"Failure",Toast.LENGTH_SHORT).show()
                Log.e(TAG, "getDynamicLink: Failure !", e)
            }


    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
       // verifySignInLink(intent)
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                redirectTo(auth.currentUser, userDBRef, this)
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        showProgressDialog()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    redirectTo(auth.currentUser, userDBRef, this)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(btnLoginWithGoogle, "Authentication Failed.", Snackbar.LENGTH_LONG).show()
                    redirectTo(auth.currentUser, userDBRef, this)
                }

                hideProgressDialog()
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signInEmail(){

        hideKeyboard(btnLoginWithEmail)

        val actionCodeSettings = ActionCodeSettings.newBuilder()
            // URL you want to redirect back to. The domain (www.example.com) for this
            // URL must be whitelisted in the Firebase Console.
            .setUrl("https://donatebloodca.page.link")
            // This must be true
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                "com.centennial.donateblood",
                true, /* installIfNotAvailable */
                "19" /* minimumVersion */)
            .build()

        if(!isValidEmail(etDonorNum.text.toString())){
            etDonorNum.error = "Enter email Id !"
            return
        }

        auth.sendSignInLinkToEmail(etDonorNum.text.toString(), actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    Stash.put("email",etDonorNum.text.toString())
                    Snackbar.make(btnLoginWithEmail,"We sent you an email with a login link. If you can't find, check your junk folder.",Snackbar.LENGTH_INDEFINITE).show()
                } else {
                    Log.e(TAG, "ErrorSendEmail: " + task.exception.toString())
                }
            }

    }

    private fun verifySignInLink(intent: Intent) {
        // [START auth_verify_sign_in_link]

        val auth = FirebaseAuth.getInstance()
        if(intent.data == null) return
        val intentLink= intent.data!!.toString()
            if(intentLink.IsNullOrEmpty())
            {
                return
            }
            // Confirm the link is a sign-in with email link.
            if (auth.isSignInWithEmailLink(intentLink)) {
                showProgressDialog()
                // Retrieve this from wherever you stored it
                val email =


                // The client SDK will parse the code from the link for you.
                auth.signInWithEmailLink(Stash.getString("email"), intentLink)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Successfully signed in with email link!")
                            val result = task.result
                            if (result != null) {
                                redirectTo(auth.currentUser, userDBRef, this)
                            }

                        } else {
                            Log.e(TAG, "Error signing in with email link", task.exception)
                            hideProgressDialog()
                        }
                        Stash.clear("email")
                        hideProgressDialog()
                    }
        }

        // [END auth_verify_sign_in_link]
    }
    // [END signin]

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            redirectTo(auth.currentUser, userDBRef, this)
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            redirectTo(auth.currentUser, userDBRef, this)
        }
    }



    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.btnLoginWithGoogle -> signInGoogle()
            R.id.btnLoginWithEmail -> signInEmail()

        }
    }

    private fun modifyGoogleButton(){
        val textView = btnLoginWithGoogle.getChildAt(0) as TextView
        textView.text = getString(R.string.continue_with_google)
    }


    companion object {
        private val TAG = this::class.java.simpleName
        private const val RC_SIGN_IN = 9001
    }
}