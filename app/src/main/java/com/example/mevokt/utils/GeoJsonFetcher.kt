package com.example.mevokt.utils

import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.GeoJson
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
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

    fun getCarsCollection(): FeatureCollection? {
        var call = this.getVehicles()
        var carsCollection: FeatureCollection? = null
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    carsCollection = FeatureCollection.fromJson(
                        response.body()?.get("data")
                            .toString())
                    println(carsCollection)
                }
                else {
                    println(response.errorBody())
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                println("fail: $t")
            }
        })
        call.cancel()
        return carsCollection
    }

    fun getParkingFeature(): Feature? {
        var call = this.getParking()
        var parkingCollection: Feature? = null
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    parkingCollection = Feature.fromJson(
                        response.body()?.get("data")
                            .toString())
                    println(parkingCollection)
                }
                else {
                    println(response.errorBody())
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                println("fail: $t")
            }
        })
        call.cancel()
        return parkingCollection
    }

}

//suspend fun main() {
//    var geoJson = GeoJsonFetcher();
//    var call = geoJson.getVehicles()
//    call.enqueue(object : Callback<JsonObject?> {
//        override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
//            if (response.isSuccessful) {
//                var carsCollections: FeatureCollection = FeatureCollection.fromJson(
//                    response.body()?.get("data")
//                    .toString())
//                println(carsCollections)
//            }
//            else {
//                println(response.errorBody())
//            }
//        }
//
//        override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
//            println("fail: $t")
//        }
//    })
//}