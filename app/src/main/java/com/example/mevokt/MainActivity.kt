package com.example.mevokt

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.mevokt.utils.GeoJsonFetcher
import com.example.mevokt.utils.LocationPermissionHelper
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    private lateinit var mapView: MapView

    private lateinit var vehiclesBtn : Button
    private lateinit var parkingBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapViewPanel)
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
        setupBtns()
    }

    private fun setupBtns() {
        vehiclesBtn = findViewById(R.id.vehiclesBtn)
        parkingBtn = findViewById(R.id.parkingBtn)
        vehiclesBtn.setOnClickListener(this)
        parkingBtn.setOnClickListener(this)
    }

    // method to stylish the map
    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            getString(R.string.mapbox_style_mapbox_streets)
        ) {
            initLocationComponent()
            setupGesturesListener()
//            vehiclesFetch(it)
//            parkingFetch(it)
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                vehiclesBtn.id -> toggleVehicles()
                parkingBtn.id -> toggleParking()
            }
        }
    }

    private fun toggleVehicles() {
        mapView.getMapboxMap().getStyle() {
            if (it.styleLayerExists("vehiclesLayer")) {
                it.removeStyleLayer("vehiclesLayer")
                it.removeStyleSource("vehicles")
            }
            else vehiclesFetch(it)
        }
    }

    private fun toggleParking() {
        mapView.getMapboxMap().getStyle() {
            if (it.styleLayerExists("parkingLayer")) {
                it.removeStyleLayer("parkingLayer")
                it.removeStyleLayer("parkingLineLayer")
                it.removeStyleSource("parkingArea")
            }
            else parkingFetch(it)
        }
    }

    private fun vehiclesFetch(style: Style) {
        style.addImage("vehicleIcon", BitmapFactory.decodeResource(resources, R.drawable.mevocar))
        val fetcher = GeoJsonFetcher()
        fetcher.getCarsCollection(style)
        Toast.makeText(this@MainActivity, "Show Vehicles", Toast.LENGTH_SHORT).show()
    }

    private fun parkingFetch(style: Style) {
        val fetcher = GeoJsonFetcher()
        fetcher.getParkingFeature(style, this@MainActivity)
        Toast.makeText(this@MainActivity, "Show Parking", Toast.LENGTH_SHORT).show()
    }
}