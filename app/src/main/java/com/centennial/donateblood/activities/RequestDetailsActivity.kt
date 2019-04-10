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
import com.centennial.donateblood.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_request_details.*
import kotlinx.android.synthetic.main.request_content.*


class RequestDetailsActivity : BaseActivity(), OnMapReadyCallback {





    private lateinit var requestID: String
    private  var mRequest: Request? = null
    private lateinit var mGoogleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details)
        setSupportActionBar(toolbar)
        title = ""




        if(intent != null && intent.extras != null && intent.extras.containsKey("request_id")){
            requestID = intent.extras!!.get("request_id")!!.toString()
        } else {
            finish()
        }



        loadRequestData(requestID)
        MapsInitializer.initialize(this)
        partialMap.onCreate(savedInstanceState)
        partialMap.onResume()
        partialMap.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap



        if(mRequest != null) {
            this.title = Constants.BGArray[mRequest!!.bloodGroup]
            var markerOptions = getMarkerFromAddress(this, mRequest!!.orgPostalCode, mRequest!!.orgName)
            if (markerOptions != null) {
                markerOptions.icon(generateBitmapDescriptorFromRes(this, R.drawable.ic_person_pin_circle_blue_48dp))
                fabDirection.setOnClickListener {
                    Snackbar.make(fabDirection, "Starting Navigation to "+mRequest!!.orgPostalCode, Snackbar.LENGTH_LONG).show()
                    startActivity(Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+mRequest!!.orgPostalCode)))
                }
                googleMap.addMarker(markerOptions)
                setRequestData(mRequest!!)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.position, 15F))

            }



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


    fun setRequestData(request: Request) {
        tvBG.text = Constants.BGArray[request.bloodGroup]
        tvName.text = request.orgName
        tvAddr.text = "${request.orgAddress}\n${request.orgPostalCode}"
        tvEmail.text = request.contactEmail
        tvMobile.text = request.contactNumber
        tvPerson.text = request.personName
        tvUnits.text = "${request.units}"


        val phoneIntent = Intent(Intent.ACTION_DIAL)
        phoneIntent.data = Uri.parse("tel:" + tvMobile.text)

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + tvEmail.text))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Blood Donate")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Request ID: $requestID \nSent from Blood Donate App")


        tvhEmail.setOnClickListener {
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }

        tvEmail.setOnClickListener {
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }
        tvhMobile.setOnClickListener {
            startActivity(phoneIntent)
        }
        tvMobile.setOnClickListener {
            startActivity(phoneIntent)
        }

        btnAccept.setOnClickListener{

            showProgressDialog()
            requestDBRef.document(requestID).get()
                .addOnSuccessListener { document ->
                    if (document.data != null) {
                        mRequest = document.toObject(Request::class.java)!!
                        if(mRequest!!.responds.size < 2){
                            if(mRequest!!.responds.size > 0){
                                mRequest!!.responds.forEach { i ->
                                    if (i.value == auth.currentUser!!.email!!){
                                        hideProgressDialog()
                                        Snackbar.make(btnAccept, "You have already accepted the request.", Snackbar.LENGTH_LONG).show()
                                        return@addOnSuccessListener
                                    }
                                }
                            }
                            mRequest!!.responds.put("user_${mRequest!!.responds.size}", auth.currentUser!!.email!!)
                            requestDBRef.document(requestID).update("responds", mRequest!!.responds).addOnSuccessListener {
                                hideProgressDialog()
                                Snackbar.make(btnAccept, "You have successfully accepted the request. Please reach to this hospital.", Snackbar.LENGTH_LONG).show()

                            }.addOnFailureListener {
                                hideProgressDialog()
                                Snackbar.make(btnAccept, "Error while accepting request. Please try again", Snackbar.LENGTH_LONG).show()

                            }
                        } else {
                            Log.e(TAG, "Total responds count:${mRequest!!.responds.size}")
                            Snackbar.make(btnAccept, "Enough number of users have already accepted this request. Thank you so much for your support.", Snackbar.LENGTH_LONG).show()
                            hideProgressDialog()
                        }
                    } else {
                        Log.d(TAG, "No such document")
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error loading document:"+exception.localizedMessage)
                    showToast("Error loading data from server", Toast.LENGTH_SHORT)
                }
        }
    }



    fun loadRequestData(requestID: String){
        showProgressDialog()
        requestDBRef.document(requestID).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {

                    mRequest = document.toObject(Request::class.java)!!
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
                finish()
            }
    }




    companion object {
         val TAG = this::class.java.simpleName
    }
}
