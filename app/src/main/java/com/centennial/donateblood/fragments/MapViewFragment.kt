package com.centennial.donateblood.fragments

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.centennial.donateblood.R
import com.centennial.donateblood.models.MapMarker
import com.centennial.donateblood.models.User
import com.centennial.donateblood.utils.Constants
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager


class MapViewFragment : Fragment(), OnMapReadyCallback {

    private lateinit var rootView: View
    private lateinit var mapView: MapView
    private lateinit var mContext: Context
    var marker: MapMarker? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.centennial.donateblood.R.layout.fragment_maps, container, false)
        setHasOptionsMenu(true)
        MapsInitializer.initialize(this.activity)
        mContext = this.context!!
        mapView = rootView.findViewById<MapView>(com.centennial.donateblood.R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
        return rootView
    }


    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnMapLoadedCallback {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    CANADA,
                    0
                )
            )
        }

        val clusterManager = ClusterManager<MapMarker>(mContext, googleMap)
        clusterManager.setCallbacks(object : ClusterManager.Callbacks<MapMarker> {
            override fun onClusterClick(@NonNull cluster: Cluster<MapMarker>): Boolean {
                Log.d(TAG, "onClusterClick")
                return false
            }

            override fun onClusterItemClick(@NonNull clusterItem: MapMarker): Boolean {
                Log.d(TAG, "onClusterItemClick")
                return false
            }
        })
        googleMap.setOnCameraIdleListener(clusterManager)

//        val clusterItems = ArrayList<MapMarker>()
//        for (i in 0..19999) {
//            clusterItems.add(
//                MapMarker(
//                    RandomLocationGenerator.generate(CANADA)
//                )
//            )
//        }

        var mapMarkers = ArrayList<MapMarker>()
        var firestore= FirebaseFirestore.getInstance()
        var userDBRef = firestore.collection(Constants.USER_DATA_REF)
        var orgDBRef = firestore.collection(Constants.HOSPITAL_DATA_REF)

        userDBRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var usr = document.toObject(User::class.java)

                    var markerOptions = getMarkerFromAddress(mContext, usr.postalCode, Constants.BGArray[usr.bloodGroup])
                    if(markerOptions != null)
                        markerOptions.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_person_pin_circle_blue_48dp))

                        googleMap.addMarker((getMarkerFromAddress(mContext, usr.postalCode, Constants.BGArray[usr.bloodGroup])))

                    }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

//        orgDBRef.get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val org = document.toObject(Hospital::class.java)
//                    if (getLocationFromAddress(mContext, org.postalCode) != null) {
//                        var marker = MapMarker(getLocationFromAddress(mContext, org.postalCode)!!)
//                        marker.title = org.name
//                        marker.snippet = org.postalCode
//                        mapMarkers.add(marker)
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting documents: ", exception)
//            }

        clusterManager.setItems(mapMarkers)
    }


    fun getMarkerFromAddress(context: Context, strAddress: String, title: String): MarkerOptions? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null

        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            location.latitude
            location.longitude

            p1 = LatLng(location.latitude, location.longitude)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var markerOptions = MarkerOptions().position(p1!!).title(title).snippet(strAddress)

        return markerOptions

    }

    fun addMarkeronMap(latlng: LatLng, title: String, description: String){
        mapView.addMarker(
            MarkerOptions()
                .position(BROOKLYN_BRIDGE)
                .title("First Pit Stop")
                .icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
        )
    }


    companion object {

        private val TAG = MapViewFragment::class.java.simpleName

        private val NETHERLANDS = LatLngBounds(
            LatLng(50.77083, 3.57361), LatLng(53.35917, 7.10833)
        )

        private val CANADA = LatLngBounds(
            LatLng(41.6751050889, -140.99778), LatLng(83.23324, -52.6480987209)
        )
    }
}
