package com.centennial.donateblood

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.fxn.stash.Stash
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp


class InitClass: Application() {

    internal var obj: Any? = null


    override fun onCreate() {
        super.onCreate()
        Stash.init(this)
        FirebaseApp.initializeApp(this)


        //   mAuth = FirebaseAuth.getInstance()
        obj = getSystemService(Context.CONNECTIVITY_SERVICE)
    }

    protected fun isInternetConnected(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = obj as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI", ignoreCase = true))
                if (ni.isConnected)
                    haveConnectedWifi = true
            if (ni.typeName.equals("MOBILE", ignoreCase = true))
                if (ni.isConnected)
                    haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }


    fun ShowInternetStatus(view: View): Boolean {
        if (!isInternetConnected()) {
            val snackbar = Snackbar
                .make(view, "Can't Connect Right Now", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY") { view ->
                    object : Thread() {
                        override fun run() {
                            if (!ShowInternetStatus(view)) {
                                Snackbar.make(view, "Connected to Network", Snackbar.LENGTH_SHORT)
                            }
                        }
                    }.start()
                }
            snackbar.show()
        }
        return isInternetConnected()
    }

}