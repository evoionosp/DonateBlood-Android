package com.centennial.donateblood.activities


import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.util.*

internal object RandomLocationGenerator {

    private val RANDOM = Random()

    fun generate(bounds: LatLngBounds): LatLng {
        val minLatitude = bounds.southwest.latitude
        val maxLatitude = bounds.northeast.latitude
        val minLongitude = bounds.southwest.longitude
        val maxLongitude = bounds.northeast.longitude
        return LatLng(
            minLatitude + (maxLatitude - minLatitude) * RANDOM.nextDouble(),
            minLongitude + (maxLongitude - minLongitude) * RANDOM.nextDouble()
        )
    }
}
