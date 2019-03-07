package com.centennial.donateblood.activities

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import com.centennial.donateblood.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager
import java.util.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        if (savedInstanceState == null) {
            setupMapFragment()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnMapLoadedCallback {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    NETHERLANDS,
                    0
                )
            )
        }

        val clusterManager = ClusterManager<MapMarker>(this, googleMap)
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

        val clusterItems = ArrayList<MapMarker>()
        for (i in 0..19999) {
            clusterItems.add(
                MapMarker(
                    RandomLocationGenerator.generate(NETHERLANDS)
                )
            )
        }
        clusterManager.setItems(clusterItems)
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.retainInstance = true
        mapFragment.getMapAsync(this)
    }

    companion object {

        private val TAG = MapsActivity::class.java.simpleName

        private val NETHERLANDS = LatLngBounds(
            LatLng(50.77083, 3.57361), LatLng(53.35917, 7.10833)
        )
    }
}
