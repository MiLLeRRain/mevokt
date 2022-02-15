package com.example.mevokt.utils

import android.provider.Settings.System.getString
import com.example.mevokt.R
import com.mapbox.geojson.GeoJson
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeoJsonFetcher {
    private val networkInterface : NetworkInterface

    companion object {
        private const val BASE_URL : String = "https://api.mevo.co.nz/public/vehicles/wellington/"
    }
    init {
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        networkInterface = retrofit.create(NetworkInterface::class.java)
    }

    suspend fun getVehicles() : GeoJsonSource {
        return networkInterface.getVehicles()
    }
}