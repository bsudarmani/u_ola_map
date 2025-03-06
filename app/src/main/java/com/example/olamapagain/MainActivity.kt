package com.example.olamapagain

import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ola.mapsdk.interfaces.OlaMapCallback
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.model.OlaMarkerOptions
import com.ola.mapsdk.model.OlaPolylineOptions
import com.ola.mapsdk.view.OlaMap
import com.ola.mapsdk.view.OlaMapView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var olamapView: OlaMapView
    lateinit var fetchCurrentLocation: FloatingActionButton
    lateinit var olaMap: OlaMap
    lateinit var startLocationEditText: EditText
    lateinit var destinationEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        olamapView = findViewById(R.id.mapView)
        fetchCurrentLocation = findViewById(R.id.floatingActionButton2)
        startLocationEditText = findViewById(R.id.editTextStartLocation)
        destinationEditText = findViewById(R.id.editTextDestination)

        olamapView.getMap(apiKey = "your_api_key",
            olaMapCallback = object : OlaMapCallback {
                override fun onMapReady(map: OlaMap) {
                    olaMap = map
                }

                override fun onMapError(error: String) {
                    Toast.makeText(this@MainActivity, "Map error: $error", Toast.LENGTH_SHORT).show()
                }
            }
        )

        fetchCurrentLocation.setOnClickListener {
            val startLocation = startLocationEditText.text.toString().trim()
            val destination = destinationEditText.text.toString().trim()
           Log.v("startlocation",startLocation)
            Log.v("endlocation",destination)

            if (startLocation.isNotEmpty() && destination.isNotEmpty()) {
                val startLatLng = getLatLngFromAddress(startLocation)
                val destinationLatLng = getLatLngFromAddress(destination)

                if (startLatLng != null && destinationLatLng != null) {
                    drawRoute(startLatLng, destinationLatLng)
                } else {
                    Toast.makeText(this, "Invalid location entered", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter both source and destination", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLatLngFromAddress(address: String): OlaLatLng? {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val locations = geocoder.getFromLocationName(address, 1)
            if (locations != null && locations.isNotEmpty()) {
                OlaLatLng(locations[0].latitude, locations[0].longitude)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun drawRoute(start: OlaLatLng, end: OlaLatLng) {
        val points = arrayListOf(start, end)
        val polylineOptions = OlaPolylineOptions.Builder()
            .setPolylineId("route1")
            .setPoints(points)
            .build()

        olaMap.addPolyline(polylineOptions)

        val startMarker = OlaMarkerOptions.Builder()
            .setMarkerId("startMarker")
            .setPosition(start)
            .setIsIconClickable(true)
            .build()

        val endMarker = OlaMarkerOptions.Builder()
            .setMarkerId("endMarker")
            .setPosition(end)
            .setIsIconClickable(true)
            .build()

        olaMap.addMarker(startMarker)
        olaMap.addMarker(endMarker)

        olaMap.moveCameraToLatLong(start, 12.0, 1000)
    }
}
