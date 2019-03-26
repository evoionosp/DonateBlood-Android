package com.centennial.donateblood.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.centennial.donateblood.models.MapMarker
import com.centennial.donateblood.utils.RandomLocationGenerator
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager
import java.util.*


class MapViewFragment : Fragment(), OnMapReadyCallback {

    private lateinit var rootView: View
    private lateinit var mapView: MapView
    private lateinit var mContext: Context


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

        val clusterItems = ArrayList<MapMarker>()
        for (i in 0..19999) {
            clusterItems.add(
                MapMarker(
                    RandomLocationGenerator.generate(CANADA)
                )
            )
        }
        clusterManager.setItems(clusterItems)
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
