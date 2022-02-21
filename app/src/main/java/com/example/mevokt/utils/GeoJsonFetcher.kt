package com.example.mevokt.utils

import android.provider.Settings.System.getString
import android.util.Log
import com.example.mevokt.R
import com.mapbox.geojson.GeoJson
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory

class GeoJsonFetcher {
    private val networkInterface : NetworkInterface

    companion object {
        private const val BASE_URL : String = "https://api.mevo.co.nz/public/"
    }

    init {
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        networkInterface = retrofit.create(NetworkInterface::class.java)
    }

    fun getVehicles() : Call<GeoJsonSource> {
        return networkInterface.getVehicles()
    }

}

suspend fun main() {
    var geoJson = GeoJsonFetcher();
    var data = geoJson.getVehicles()
    println(data.await())
}