package com.example.mevokt.utils
import com.example.mevokt.R
import com.google.gson.JsonObject
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.GeoJson
import com.mapbox.geojson.gson.GeoJsonAdapterFactory
import com.mapbox.geojson.gson.GeometryGeoJson
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET

private const val VEHICLES_WELLINGTON : String = "vehicles/wellington"
private const val PARKING_WELLINGTON : String = "parking/wellington"

interface NetworkInterface {

    @GET(VEHICLES_WELLINGTON)
    fun getVehicles(): Call<JsonObject>

    @GET(PARKING_WELLINGTON)
    fun getParking(): Call<JsonObject>

}