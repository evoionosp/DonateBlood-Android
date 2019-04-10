package com.centennial.donateblood.fragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.centennial.donateblood.R
import com.centennial.donateblood.models.User
import com.centennial.donateblood.utils.Constants
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore


class DonorMapFragment : BaseFragment(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {



    private lateinit var rootView: View
    private lateinit var mapView: MapView
    private lateinit var mContext: Context
    private lateinit var mGoogleMap: GoogleMap



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.centennial.donateblood.R.layout.fragment_maps, container, false)
        setHasOptionsMenu(true)
        MapsInitializer.initialize(this.activity)
        mContext = this.context!!
        mapView = rootView.findViewById<MapView>(com.centennial.donateblood.R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        activity!!.title = getString(R.string.donors_nearby)

        isGooglePlayServicesAvailable()

        return rootView
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        googleMap.setOnMapLoadedCallback {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    CANADA,
                    0
                )
            )
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mGoogleMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        }
        else {
            mGoogleMap.isMyLocationEnabled = true
        }
        var firestore= FirebaseFirestore.getInstance()
        var userDBRef = firestore.collection(Constants.USER_DATA_REF)
        var orgDBRef = firestore.collection(Constants.HOSPITAL_DATA_REF)

        userDBRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var usr = document.toObject(User::class.java)

                    if (getMarkerFromAddress(mContext, usr.postalCode, Constants.BGArray[usr.bloodGroup]) != null) {
                        var markerOptions =
                            getMarkerFromAddress(mContext, usr.postalCode, Constants.BGArray[usr.bloodGroup])
                        markerOptions!!.icon(
                            generateBitmapDescriptorFromRes(mContext, R.drawable.ic_person_pin_circle_blue_48dp)
                        )
                        googleMap.addMarker(markerOptions)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.position, 13F))

                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

     /*   orgDBRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var org = document.toObject(Hospital::class.java)
                    var markerOptions = getMarkerFromAddress(mContext, org.postalCode, org.name)
                    if(markerOptions != null)
                        markerOptions.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_local_hospital_red_48dp))

                    googleMap.addMarker(markerOptions)

                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            } */

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








    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(activity!!)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    })
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mGoogleMap.isMyLocationEnabled = true
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }



    override fun onLocationChanged(location: Location) {

        //move map camera
        val cameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(17.0.toFloat()).build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        mGoogleMap.moveCamera(cameraUpdate)
        var latLng = LatLng(location.latitude, location.longitude)
        mGoogleMap.addMarker( MarkerOptions().position(latLng))
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15F))


    }

    override fun onConnected(p0: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun isGooglePlayServicesAvailable(): Boolean {
        var status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext)
        if (ConnectionResult.SUCCESS == status) {
            return true
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show()
            return false
        }
    }



    companion object {

        private val TAG = DonorMapFragment::class.java.simpleName
        private val MY_PERMISSIONS_REQUEST_LOCATION = 99

        private val NETHERLANDS = LatLngBounds(
            LatLng(50.77083, 3.57361), LatLng(53.35917, 7.10833)
        )

        private val CANADA = LatLngBounds(
            LatLng(41.6751050889, -140.99778), LatLng(83.23324, -52.6480987209)
        )
    }
}
