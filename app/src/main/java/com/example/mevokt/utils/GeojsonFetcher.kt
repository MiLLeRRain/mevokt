package com.example.mevokt.utils

import com.example.mevokt.R
import com.mapbox.geojson.GeoJson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeojsonFetcher {
    private val networkInterface : NetworkInterface

    companion object {
        private const val VEHICLES_URL : String = R.string.vehicles_api_url.toString()
        private const val PARKING_URL : String = R.string.parking_api_url.toString()
    }
    init {
        val retrofit = Retrofit
            .Builder()
            .baseUrl(VEHICLES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        networkInterface = retrofit.create(NetworkInterface::class.java)
    }

    suspend fun getVehicles() : GeoJson {
        return networkInterface.getVehicles()
    }
}