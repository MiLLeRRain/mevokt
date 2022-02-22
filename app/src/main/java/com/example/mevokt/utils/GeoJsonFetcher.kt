package com.example.mevokt.utils

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import com.example.mevokt.MainActivity
import com.example.mevokt.R
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.GeoJson
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
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

    private fun getVehicles() : Call<JsonObject> {
        return networkInterface.getVehicles()
    }

    private fun getParking(): Call<JsonObject> {
        return networkInterface.getParking()
    }

    fun getCarsCollection(style: Style) {
        var call = this.getVehicles()
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    val carsCollection = FeatureCollection.fromJson(
                        response.body()?.get("data")
                            .toString())
                    val source = geoJsonSource("vehicles"){
                        data(carsCollection.toJson())
                    }
                    style.addSource(source)
                    style.addLayer(symbolLayer("vehiclesLayer", "vehicles") {
                        iconImage("vehicleIcon")
                        iconAnchor(IconAnchor.BOTTOM)
                        iconAllowOverlap(true)
                        iconSize(0.2)
                    })
                }
                else {
                    println(response.errorBody())
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                println("fail: $t")
            }
        })
    }

    fun getParkingFeature(style: Style, mainActivity: MainActivity) {
        var call = this.getParking()
        call.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("ResourceAsColor")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    val parkingFeature = Feature.fromJson(
                        response.body()?.get("data")
                            .toString())
                    val source = geoJsonSource("parkingArea"){
                        data(parkingFeature.toJson())
                    }
                    style.addSource(source)
                    style.addLayer(fillLayer("parkingLayer", "parkingArea") {
                        fillColor(ContextCompat.getColor(mainActivity, R.color.mevo_primary))
                        fillOpacity(0.5)
                    })
                    style.addLayer(lineLayer("parkingLineLayer", "parkingArea") {
                        lineColor(ContextCompat.getColor(mainActivity, R.color.mevo_accent))
                        lineWidth(3.0)
                        lineOpacity(0.5)
                    })
                }
                else {
                    println(response.errorBody())
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                println("fail: $t")
            }
        })
//        call.cancel()
    }

}