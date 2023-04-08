package com.rivaldofez.storymessage.util

import android.content.Context
import android.location.Geocoder

object LocationUtility {
    fun parseAddressLocation(
        context: Context,
        lat: Double,
        lon: Double
    ): String {
        val geocoder = Geocoder(context)
        val geoLocation =
            geocoder.getFromLocation(lat, lon, 1)
        return if (geoLocation?.size!! > 0) {
            val location = geoLocation?.get(0)
            val fullAddress = location?.getAddressLine(0)
            StringBuilder("📌 ")
                .append(fullAddress).toString()
        } else {
            "📌 Location Unknown"
        }
    }
}