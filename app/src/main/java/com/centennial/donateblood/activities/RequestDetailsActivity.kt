package com.centennial.donateblood.activities

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.centennial.donateblood.R
import com.centennial.donateblood.models.Request
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_request_details.*


class RequestDetailsActivity : BaseActivity(), OnMapReadyCallback {





    private lateinit var requestID: String
    private  var request: Request? = null
    private lateinit var mGoogleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details)
        setSupportActionBar(toolbar)
        title = "Request Details Activity"




        if(intent != null && intent.extras != null && intent.extras.containsKey("request_id")){
            requestID = intent.extras!!.get("request_id")!!.toString()
        } else {
            // todo: redirect
        }

        requestID = "tJhGwOpILMWKGfhAJC5Q"

        showToast("Request: "+requestID, Toast.LENGTH_LONG)



        loadRequestData(requestID)
        MapsInitializer.initialize(this)
        partialMap.onCreate(savedInstanceState)
        partialMap.onResume()
        partialMap.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap

        if(request != null) {
            var markerOptions = getMarkerFromAddress(this, request!!.orgPostalCode, request!!.orgName)
            if (markerOptions != null) {
                markerOptions.icon(generateBitmapDescriptorFromRes(this, R.drawable.ic_person_pin_circle_blue_48dp))

                googleMap.addMarker(markerOptions)

                fabDirection.setOnClickListener {
                    Snackbar.make(fabDirection, "Starting Navigation to "+request!!.orgPostalCode, Snackbar.LENGTH_LONG).show()
                    startActivity(Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+request!!.orgPostalCode)))
                }
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions!!.position, 15F))
        }

    }






    fun getMarkerFromAddress(context: Context, strAddress: String, title: String): MarkerOptions? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null

        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            } else {
                val location = address[0]
                location.latitude
                location.longitude

                p1 = LatLng(location.latitude, location.longitude)

                var markerOptions = MarkerOptions().position(p1).title(title).snippet(strAddress)
                return markerOptions
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }



    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val extras = intent.extras
        title = if (extras != null) {
            if (extras.containsKey("request_id")) {
                extras.get("request_id").toString()
            } else {
                Log.e(TAG, "extras: Not null, request_id: not found")
                "notfound: request"
            }
        } else {
            Log.e(TAG, "extras: null")
            "notfound: extras"
        }


    }

    fun loadRequestData(requestID: String){
        showProgressDialog()
        requestDBRef.document(requestID).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {

                    request = document.toObject(Request::class.java)!!
                    onMapReady(mGoogleMap)
                    hideProgressDialog()

                } else {
                    hideProgressDialog()
                    Log.d(TAG, "No such document")
                    //startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            .addOnFailureListener { exception ->
                hideProgressDialog()
                Log.d(TAG, "Error loading document:"+exception.localizedMessage)
                //startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    companion object {
         val TAG = this::class.java.simpleName
    }
}
