package com.example.mevokt.utils
import com.example.mevokt.R
import com.mapbox.geojson.GeoJson
import retrofit2.http.GET

private const val VEHICLES_URL : String = R.string.vehicles_api_url.toString()
private const val PARKING_URL : String = R.string.parking_api_url.toString()

interface NetworkInterface {

    @GET(VEHICLES_URL)
    suspend fun getVehicles(): GeoJson

}